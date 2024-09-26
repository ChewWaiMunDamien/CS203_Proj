import React, { useState } from 'react'; 
import axios from 'axios'; 
import { useNavigate } from 'react-router-dom'; 
import { 
	MDBContainer, 
	MDBInput, 
	MDBBtn, 
} from 'mdb-react-ui-kit'; 
import { jwtDecode } from 'jwt-decode';

function Login() { 
    let navigate=useNavigate();
	const [username, setUsername] = useState(''); 
	const [password, setPassword] = useState(''); 
	const [error, setError] = useState(''); 
	const history = useNavigate(); 

	const handleLogin = async () => { 
		try { 
			if (!username || !password) { 
				setError('Please enter both username and password.'); 
				return; 
			} 
            
			const response = await axios.post('http://localhost:8080/auth/signin', { username, password }); 
			
			console.log('Login successful:', response.data.jwt);
			
			const token = response.data.jwt; 
			localStorage.setItem('token', token);
			const expiryTime = new Date().getTime() + 900 * 1000; //expiry time to 5 mins for testing
			localStorage.setItem('tokenExpiry', expiryTime);
			// Decode the token and print the whole payload
			const decodedToken = jwtDecode(token); 
			console.log('Decoded Token:', decodedToken); 
			
			const userRole = decodedToken.authorities;
			console.log(userRole + " HELLO");
			
			if (userRole == "admin"){
				navigate("/admin/tournament");
			} else if (userRole=="user"){
				navigate("/user/tournament");
			}
			
		} catch (error) { 
			console.error('Login failed:', error.response ? error.response.data : error.message); 
			setError('Invalid username or password.'); 
		} 
	}; 

	return ( 
		<div className="d-flex justify-content-center align-items-center vh-100"> 
			<div className="border rounded-lg p-4" style={{ width: '500px', height: 'auto' }}> 
				<MDBContainer className="p-3"> 
					<h2 className="mb-4 text-center">Login Page</h2> 
					<MDBInput wrapperClass='mb-4' placeholder='Email address' id='email' value={username} type='email' onChange={(e) => setUsername(e.target.value)} /> 
					<MDBInput wrapperClass='mb-4' placeholder='Password' id='password' type='password' value={password} onChange={(e) => setPassword(e.target.value)} /> 
					{error && <p className="text-danger">{error}</p>} {/* Render error message if exists */} 
					<button className="mb-4 d-block btn-primary" style={{ height:'50px',width: '100%' }} onClick={handleLogin}>Sign in</button> 
					<div className="text-center"> 
						<p>Not a member? <a href="/register" >Register</a></p> 
					</div> 
				</MDBContainer> 
			</div> 
		</div> 
	); 
} 

export default Login; 
