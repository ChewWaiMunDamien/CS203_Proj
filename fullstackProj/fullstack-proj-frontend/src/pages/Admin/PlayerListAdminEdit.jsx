import React, {useState, useEffect} from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import '../admin/Register.css';
import { Link, useNavigate, useParams } from "react-router-dom";
import axios from "axios"
import { jwtDecode } from 'jwt-decode';

export default function PlayerListAdminEdit() {
    let navigate=useNavigate();
    const {id} = useParams();
    const [user,setUser] = useState({id:"", username:"", elo:0});
    const{username, elo} = user;
    const [usernameTaken, setUsernameTaken] = useState(false);
    const clearTokens = () => {
        localStorage.removeItem('token'); // Remove the main token
        localStorage.removeItem('tokenExpiry'); // Remove the token expiry time
        // Add any other tokens you want to clear here
        // localStorage.removeItem('anotherToken');
        // tokenKeys.forEach(key => {
        //     localStorage.removeItem(key);
        // });
    };


    const onInputChange=(e)=>{
        setUser({...user, [e.target.name]:e.target.value});
        

        // if (e.target.name === "email") {
        //     checkEmailAvailability(e.target.value);
        // }
    }

    const loadUser = async () => {
        const result= await axios.get(`http://localhost:8080/auth/user/${id}`);
        setUser(result.data);
    }
    
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


    const onSubmit= async (e)=>{
        e.preventDefault();
        

        const userData = {
            username,
            elo
        };

        try {
            await axios.put(`http://localhost:8080/auth/user/${id}`, userData);
            navigate("/admin/playerlist");
        } catch (error) {
            console.error("There was an error editing the User!", error);
        }
        
    }
    useEffect(() => {
        const fetchData = async () => {
            const token = localStorage.getItem('token');
            console.log(token +" hello");
            
            if (!token || isTokenExpired()|| !isAdminToken(token)) {
                console.log("fail");
               
                clearTokens();
                window.location.href = '/'; // Redirect to login if token is missing or expired
                return;
            }
            
        };

        fetchData();
        loadUser(id);

    }, []);

    return (
        <div className="register-container">
          <div className="form-container">
            <form onSubmit={(e) => onSubmit(e)}>
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control form-control-lg"
                id="floatingInput"
                placeholder="name@example.com"
                value={username}
                onChange={(e) =>onInputChange(e)}
                name="username"
              ></input>
              <label htmlFor="username">Username</label>
            </div>
            
            <div className="form-floating mb-3">
              <input
                type="number"
                className="form-control"
                id="floatingUsername"
                placeholder="elo"
                value={elo}
                onChange={(e) =>onInputChange(e)}
                name="elo"
              />
              <label htmlFor="elo">elo</label>

            </div>
            
                
            <button type="submit" className='btn btn-outline-primary mt-3'>Edit User</button>
            </form>
            <Link type="cancel" className='btn btn-outline-danger' to='/admin/tournament' id="returnrBtn">Cancel</Link>
          </div>
        </div>
      );
}
