import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

import { Link, useParams } from 'react-router-dom';

const PlayerListAdmin = () => {
    const[user,setUser]=useState([]);
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

        fetchData();
        
        loadUsers();

    }, []);

    if (error) {
        return <div>{error}</div>;
    }

    const loadUsers= async()=>{
        const result = await axios.get("http://localhost:8080/auth/users");
      
        
        setUser(result.data);
    };

    return (
        <div style={{ padding: '20px' }}>
            {data ? (
                <div className="">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h1>Player List</h1>
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <Link className="btn mb-4 btn-outline-primary" style={{ height:'40px',width: '100px',borderRadius: '20px', maxWidth:'150px' }} to="/admin/playerlist/create">Create</Link>
                        </div>
                    </div>
                    <div className="container">
                        <table className="table table-striped">
                            <thead>
                                <tr>
                                    <th scope="col">#</th>
                                    <th scope="col">ID</th>
                                    <th scope="col">Username</th>
                                    <th scope="col">Elo</th>
                                    <th scope="col">Role</th>
                                    {/* <th scope="col">Status</th>
                                    <th scope="col">size</th> */}
                                </tr>
                            </thead>
                            <tbody>
                                {   user.map((user, index) =>
                                    <tr>
                                        <th scope="row" key={index} > {index + 1}</th>
                                        <td>{user.id}</td>
                                        <td>{user.username}</td>
                                        <td>{user.elo}</td>
                                        <td>{user.role}</td>
                                        {/* <td>{tournament.status}</td>
                                        <td>{tournament.size}</td> */}
                                        <td style={{display:"flex", justifyContent:"flex-end"}}>
                                            <Link className="btn btn-outline-primary" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center', marginRight:"20px" }} to={`/admin/user/edit/${user.id}`}>Edit</Link>
                                            <button className="btn btn-outline-danger" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center' }} onClick={() => deleteUser(user.id)}>Delete</button>
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

export default PlayerListAdmin;