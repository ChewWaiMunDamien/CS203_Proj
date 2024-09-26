import React, {useState} from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import '../pages/Register.css';
import { Link, useNavigate } from "react-router-dom";
import axios from "axios"

export default function Register() {
    let navigate=useNavigate();

    const [user,setUser] = useState({username:"", password:"", email:"", confirmPassword:"", role:"user"});
    const{username, password, email, confirmPassword, role} = user;
    const [usernameTaken, setUsernameTaken] = useState(false);
    const [emailTaken, setEmailTaken] = useState(false);


    const onInputChange=(e)=>{
        setUser({...user, [e.target.name]:e.target.value});
        if (e.target.name === "username") {
            checkUsernameAvailability(e.target.value);
        }

        if (e.target.name === "email") {
            checkEmailAvailability(e.target.value);
        }
    }
    

    const isValidEmail = (email) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    };

    const isDomainValid = async (email) => {
        const domain = email.split('@')[1];
        if (!domain) return false;

        try {
            const response = await fetch(`https://dns.google/resolve?name=${domain}&type=MX`);
            const data = await response.json();
            return data.Answer && data.Answer.length > 0; // Check if MX records exist
        } catch {
            return false;
        }
    };

    const checkUsernameAvailability = async (username) => {
        try {
            const response = await axios.get(`http://localhost:8080/auth/check-username?username=${username}`);
            setUsernameTaken(response.data.exists);
        } catch (error) {
            console.error('Error checking username availability', error);
        }
    };

    const checkEmailAvailability = async (email) => {
        try {
            const response = await axios.get(`http://localhost:8080/auth/check-email?email=${email}`);
            setEmailTaken(response.data.exists);
        } catch (error) {
            console.error('Error checking email availability', error);
        }
    };

    const onSubmit= async (e)=>{
        e.preventDefault();
        console.log(role);
        if (!isValidEmail(email)) {
            alert("Invalid email address");
            return;
        }
        if (usernameTaken) {
            alert("Username is already taken");
            return;
        }

        if (emailTaken) {
            alert("Email is already taken");
            return;
        }

        const domainValid = await isDomainValid(email);
        if (!domainValid) {
            alert("Email domain does not exist or cannot receive emails");
            return;
        }

        if (password !== confirmPassword) {
            alert("Passwords do not match");
            return;
        } else if (password.length <= 6){
            alert("Password needs to be more than 6 characters");
            return;
        }
        // System.out.println(role);
        const userData = {
            username,
            password,
            email,
            role
        };

        try {
            await axios.post("http://localhost:8080/auth/signup", userData);
            navigate("/");
        } catch (error) {
            console.error("There was an error registering the user!", error);
        }
        
    }

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
                value={email}
                onChange={(e) =>onInputChange(e)}
                name="email"
              ></input>
              <label htmlFor="Email">Email address</label>
            </div>
            
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control"
                id="floatingUsername"
                placeholder="Username"
                value={username}
                onChange={(e) =>onInputChange(e)}
                name="username"
              />
              <label htmlFor="Username">Username</label>

            </div>
            <div className="form-floating">
              <input
                type="password"
                className="form-control"
                placeholder="Password"
                value={password}
                onChange={(e) =>onInputChange(e)}
                name="password"
              />
              <label htmlFor="Password">Password</label>
            </div>
            <div className="form-floating mt-3">
              <input
                type="password"
                className="form-control"
                id="floatingConfirmPassword"
                placeholder="Confirm Password"
                value={confirmPassword}
                onChange={onInputChange}
                name="confirmPassword"
              />
              <label htmlFor="PasswordConfirm">Confirm Your Password</label>
            </div>
                <div className="form-floating mb-3 mt-3">
                        <select
                            className="form-control"
                            id="floatingRole"
                            //value={role}
                            onChange={(e) => onInputChange(e)}
                            name="role"
                        >
                            <option value="user">User</option>
                            <option value="admin">Admin</option>
                        </select>
                        <label htmlFor="Role">Role</label>
                </div>
            <button type="submit" className='btn btn-outline-primary mt-3'>Register</button>
            </form>
            <Link type="cancel" className='btn btn-outline-danger' to='/' id="returnrBtn">Cancel</Link>
          </div>
        </div>
      );
}
