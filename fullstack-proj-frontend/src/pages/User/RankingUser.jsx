import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

import { Link, useParams } from 'react-router-dom';

const RankingUser = () => {
    const [user, setUser] = useState([]);
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

    const deleteUser = async (id) => {
        try {

            await axios.delete(`http://localhost:8080/auth/user/${id}`);
            // Refresh the tournament list after deletion
            loadUsers();
        } catch (error) {
            setError('An error occurred while deleting the user.');
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
            return decodedToken.authorities === 'user'; // Adjust this based on your token's structure
        } catch (error) {
            return false;
        }
    };

    useEffect(() => {
        const fetchData = async () => {
            const token = localStorage.getItem('token');
            console.log(token + " hello");

            if (!token || isTokenExpired() || !isAdminToken(token)) {
                clearTokens();
                window.location.href = '/'; // Redirect to login if token is missing or expired
                return;
            }

            try {
                const response = await axios.get('http://localhost:8080/auth/users', {
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

        const initSSE = () => {
            const eventSource = new EventSource('http://localhost:8080/sse/users');

            eventSource.onmessage = (event) => {
                const users = JSON.parse(event.data);
                const filteredUsers = users
                .filter(user => user.role === 'user')
                .sort((a, b) => b.elo - a.elo); // Sort by highest Elo first
                setUser(filteredUsers);
            }

            eventSource.onerror = (error) => {
                console.error("SSE failure:", error);
                setError("SSE error");
                eventSource.close();
            }

            return () => {
                eventSource.close();
            };
        }



        fetchData();
        initSSE();
        loadUsers();

    }, []);

    
    const loadUsers = async () => {
        try {
            const result = await axios.get("http://localhost:8080/auth/users");
            const filteredUsers = result.data
                .filter(user => user.role === 'user')
                .sort((a, b) => b.elo - a.elo); // Sort by highest Elo first
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
                        <h1>Ranking</h1>

                    </div>
                    <div className="container">
                        <table className="table table-striped">
                            <thead>
                                <tr>
                                    <th scope="col">#</th>
                                    <th scope="col">Username</th>
                                    <th scope="col">Elo</th>
                                    {/* <th scope="col">Status</th>
                                    <th scope="col">size</th> */}
                                </tr>
                            </thead>
                            <tbody>
                                {user.map((user, index) =>
                                    <tr>
                                        <th scope="row" key={index} > {index + 1}</th>
                                        <td>{user.username}</td>
                                        <td>{user.elo}</td>
                                        {/* <td>{tournament.status}</td>
                                        <td>{tournament.size}</td> */}

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

export default RankingUser;