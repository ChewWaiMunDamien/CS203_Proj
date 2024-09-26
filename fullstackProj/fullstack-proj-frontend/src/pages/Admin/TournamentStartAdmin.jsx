import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

import { Link, useParams, useLocation } from 'react-router-dom';

const TournamentStartAdmin = () => {
    const location = useLocation();
    const { user } = location.state || { user: [] };
    const [group, setGroup] = useState([]); 
    const[tournament,setTournament]=useState([]);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const { id } = useParams();
    const[rounds, setRounds] = useState(1);
    const[maxRound, setMaxRounds] = useState(0);
    const clearTokens = () => {
        localStorage.removeItem('token'); // Remove the main token
        localStorage.removeItem('tokenExpiry'); // Remove the token expiry time
        // Add any other tokens you want to clear here
        // localStorage.removeItem('anotherToken');
        // tokenKeys.forEach(key => {
        //     localStorage.removeItem(key);
        // });
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

    const createGroupsOfTwo = (users) => {
        const groups = [];
        for (let i = 0; i < users.length; i += 2) {
            const group = users.slice(i, i + 2);
            groups.push(group);
        }
        return groups;
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
        setGroup(createGroupsOfTwo(user));
        setMaxRounds(tournament.noOfRounds);
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
                        
                    </div>
                    <h3 style={{marginTop:"20px"}}>Groupings</h3>
                    
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <h5 className="mb-3 mt-3">Rounds: {rounds}/{maxRound}</h5>
                        <Link className="btn btn-outline-primary" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center' }} to={`/admin/tournament/edit/${tournament.id}`}>Edit</Link>
                        <button className="btn btn-outline-success" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center' }} onClick={() => deleteTournament(tournament.id)}>Next</button>
                    </div>
                    <div className="row" style={{display:"flex"}}>
                    {group.map((group, index) => (
                        <div className="col-sm-6 mb-3" key={index}>
                            <div className="card">
                                <div className="card-body">
                                    <h5 className="card-title">Group {index + 1}</h5>
                                    <p className="card-text">
                        {group[0] ? group[0].username : 'No user'} vs {group[1] ? group[1].username : 'No user'}
                    </p>
                </div>
            </div>
        </div>
    ))}   
                    </div>

                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div>
                            <h3 style={{marginTop:"20px"}}>Scoreboard</h3>
                            <h5 style={{color:'gray'}}>{tournament.currentSize}/{tournament.size}</h5>
                        </div>
                        <div style={{ display: 'flex', gap: '10px' }}>
                        <Link className="btn btn-outline-success" style={{ height:'40px',width: '200px',borderRadius: '20px', maxWidth:'200px', textAlign: 'center' }} to={`/admin/tournament/${tournament.id}/add_particpant`} state={{user}}>Start</Link>
                        
                        
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
                                    user.map((user) =>
                                        <tr>   
                                            <td>{user.username}</td>
                                            <td>{user.elo}</td>                                    
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

export default TournamentStartAdmin;