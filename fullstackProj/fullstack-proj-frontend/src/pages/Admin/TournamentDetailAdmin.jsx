import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

import { Link, useParams } from 'react-router-dom';

const TournamentDetailAdmin = () => {
    const[user,setUser]=useState([]);
    const[tournament,setTournament]=useState([]);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const { id } = useParams();
    const clearTokens = () => {
        localStorage.removeItem('token'); // Remove the main token
        localStorage.removeItem('tokenExpiry'); // Remove the token expiry time
        // Add any other tokens you want to clear here
        // localStorage.removeItem('anotherToken');
        // tokenKeys.forEach(key => {
        //     localStorage.removeItem(key);
        // });
    };

    const deleteTournament = async (id) => {
        try {
            if (user.length > 0) {
                setError('Cannot delete a tournament with participants.');
                return;
            }
            await axios.delete(`http://localhost:8080/auth/tournament/${id}`);
            // Refresh the tournament list after deletion
            loadTournaments();
        } catch (error) {
            setError('An error occurred while deleting the tournament.');
        }
    };

    const removePlayer = async (user_id) => {
        try {
            
            await axios.put(`http://localhost:8080/auth/tournament/${id}/participant/delete?user_id=${user_id}`);
            await axios.put(`http://localhost:8080/auth/user/${user_id}/participating_tournament/remove?tournament_id=${id}`);
            // Refresh the tournament list after deletion
            loadTournaments();
            loadUsers();
        } catch (error) {
            setError('An error occurred while deleting the tournament.');
        }
    };


    const isTokenExpired = () => {
        const expiryTime = localStorage.getItem('tokenExpiry');
        if (!expiryTime) return true;
        return new Date().getTime() > expiryTime;
    };

    const isAdminToken = (token) => {
        try {
            const decodedToken = jwtDecode(token);
            console.log(decodedToken)
            console.log(decodedToken.authorities)
            return decodedToken.authorities === 'admin'; // Adjust this based on your token's structure
        } catch (error) {
            return false;
        }
    };

    useEffect(() => {
        const fetchData = async () => {
            const token = localStorage.getItem('token');
            console.log(token +" hello");
            
            if (!token || isTokenExpired()|| !isAdminToken(token)) {
                clearTokens();
                window.location.href = '/'; // Redirect to login if token is missing or expired
                return;
            }

            try {
                const response = await axios.get('http://localhost:8080/auth/admin/tournament', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setData(response.data);
            } catch (error) {
                if (error.response && error.response.status === 401) {
                    clearTokens();
                    localStorage.removeItem('token'); // Remove token from localStorage
                    window.location.href = '/'; // Redirect to login if token is invalid
                } else {
                    setError('An error occurred while fetching data.');
                }
            }
        };

        fetchData();
        // Add event listener to clear tokens on session end
        //window.addEventListener('beforeunload', clearTokens);

        // Cleanup event listener on component unmount
        loadTournaments();
        loadUsers();

    }, []);

    if (error) {
        return <div>{error}</div>;
    }

    const loadTournaments= async()=>{
        const result = await axios.get(`http://localhost:8080/auth/tournament/${id}`);
        setTournament(result.data);
    };
    const loadUsers= async()=>{
        
        try {
            // Fetch the list of user IDs
            const userIds = await axios.get(`http://localhost:8080/auth/tournament/${id}/participant`);
            console.log(userIds);

            // Fetch details for each user ID
            const userDetailsPromises = userIds.data.map(userId => 
                axios.get(`http://localhost:8080/auth/user/${userId}`)
            );
            console.log(userDetailsPromises);
            const userDetailsResponses = await Promise.all(userDetailsPromises);
            const userDetails = userDetailsResponses.map(response => response.data);

            // Filter users based on role and participation in the current tournament
            const filteredUsers = userDetails.filter(user => user.role === 'user')
            .sort((a, b) => b.elo - a.elo); // Sort by highest Elo first
            setUser(filteredUsers);
           
                


            setUser(filteredUsers);
        } catch (error) {
            setError("Error loading users");
            console.error("Error loading users:", error);
        }
    };
    return (
        <div style={{ padding: '20px' }}>
            {data ? (
                <div className="">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div>
                            <h1>{tournament.tournament_name}</h1>
                            <h5 style={{color:'gray'}}>Tournament ID: {tournament.id}</h5>
                        </div>
                        <div style={{ display: 'flex', gap: '10px' }}>
                        <Link className="btn btn-outline-primary" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center' }} to={`/admin/tournament/edit/${tournament.id}`}>Edit</Link>
                        <button className="btn btn-outline-danger" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center' }} onClick={() => deleteTournament(tournament.id)}>Delete</button>
                        </div>
                    </div>
                    <h3 style={{marginTop:"20px"}}>Overview</h3>
                    <div className="row">
                    <div className="col-sm-6">
                        <div className="card">
                        <div className="card-body">
                            <h5 className="card-title">Game</h5>
                            <p className="card-text">Chess</p>
                        </div>
                        </div>
                    </div>
                    <div className="col-sm-6">
                        <div className="card">
                        <div className="card-body">
                            <h5 className="card-title">Format</h5>
                            <p className="card-text">Swiss</p>
                        </div>
                        </div>
                    </div>
                    <div className="col-sm-6 mt-3">
                        <div className="card">
                        <div className="card-body">
                            <h5 className="card-title">Number of rounds</h5>
                            <p className="card-text">{tournament.noOfRounds}</p>
                        </div>
                        </div>
                    </div>
                    <div className="col-sm-6 mt-3">
                        <div className="card">
                        <div className="card-body">
                            <h5 className="card-title">Start Date</h5>
                            <p className="card-text">{tournament.date}</p>
                        </div>
                        </div>
                    </div>
                    </div>

                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div>
                            <h3 style={{marginTop:"20px"}}>Player list</h3>
                            <h5 style={{color:'gray'}}>{tournament.currentSize}/{tournament.size}</h5>
                        </div>
                        <div style={{ display: 'flex', gap: '10px' }}>
                        <Link className="btn btn-outline-success" style={{ height:'40px',width: '200px',borderRadius: '20px', maxWidth:'200px', textAlign: 'center' }} to={`/admin/tournament/${tournament.id}/start_page`} state={{user}}>Start</Link>
                        <Link className="btn btn-outline-primary" style={{ height:'40px',width: '200px',borderRadius: '20px', maxWidth:'200px', textAlign: 'center' }} to={`/admin/tournament/${tournament.id}/add_particpant`}>Add players</Link>
                        
                        </div>
                    </div>

                    <div className="container">
                        <table className="table table-striped">
                            <thead>
                                <tr>
                                    
                                    
                                    <th scope="col">Player Name</th>
                                    <th scope="col">Elo</th>
                                </tr>
                            </thead>
                            <tbody>
                                {   
                                    user.map((user, index) =>
                                        <tr>
                                            
                                            <td>{user.username}</td>
                                            <td>{user.elo}</td>
                                        <td style={{display:"flex", justifyContent:"flex-end"}}>
               
                                            <button className="btn btn-outline-danger" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center' }} onClick={(event) => {removePlayer(user.id);
                                            event.stopPropagation();
                                            }}>Remove</button>
                                        </td>
                                        
                                    </tr>
                                 )}
                            </tbody>
                        </table>
                    </div>
                </div>
            ) : (
                <div>Loading...</div>
            )}
        </div>
    );
};

export default TournamentDetailAdmin;