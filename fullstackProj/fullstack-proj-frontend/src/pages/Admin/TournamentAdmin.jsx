import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

import { Link, useParams, useNavigate } from 'react-router-dom';

const TournamentAdmin = () => {
    const navigate = useNavigate();
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
            if (tournament.participants.length > 0) {
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

    }, []);

    if (error) {
        return <div>{error}</div>;
    }

    const loadTournaments= async()=>{
        const result = await axios.get("http://localhost:8080/auth/tournaments");
        setTournament(result.data);
    };
    const handleRowClick = (id) => {
        navigate(`/admin/tournament/${id}`);
    };

    return (
        <div style={{ padding: '20px' }}>
            {data ? (
                <div className="">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h1>Tournament</h1>
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <Link className="btn mb-4 btn-outline-primary" style={{ height:'40px',width: '100px',borderRadius: '20px', maxWidth:'150px' }} to="/admin/tournament/create">Create</Link>
                        </div>
                    </div>
                    <div className="container">
                        <table className="table table-striped">
                            <thead>
                                <tr>
                                    <th scope="col">#</th>
                                    <th scope="col">ID</th>
                                    <th scope="col">Tournament Name</th>
                                    <th scope="col">Date</th>
                                    <th scope="col">Status</th>
                                    <th scope="col">size</th>
                                </tr>
                            </thead>
                            
                            <tbody>
                                {   tournament.map((tournament, index) =>
                                
                                    <tr key={tournament.id} onClick={() => handleRowClick(tournament.id)}>
                                        <th scope="row"> {index + 1}</th>
                                        <td>{tournament.id}</td>
                                        <td>{tournament.tournament_name}</td>
                                        <td>{tournament.date}</td>
                                        <td>{tournament.status}</td>
                                        <td>{tournament.currentSize}  / {tournament.size}</td>
                                        <td style={{display:"flex", justifyContent:"flex-end"}}>
                                            <Link className="btn btn-outline-primary" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center', marginRight:"20px" }} to={`/admin/tournament/edit/${tournament.id}`}onClick={(event) => event.stopPropagation()}>Edit</Link>
                                            <button className="btn btn-outline-danger" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center' }} onClick={(event) => {deleteTournament(tournament.id);
                                            event.stopPropagation();
                                            }}>Delete</button>
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

export default TournamentAdmin;