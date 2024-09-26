import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const AdminNavbar = () => {
    const location = useLocation();

    return (
        <nav className="navbar navbar-expand-lg navbar-info bg-light" >
            <div className="container-fluid" >
                <a className="navbar-brand" href="/admin/tournament" style={{fontSize:"1.5rem"}}>ChessComp.io</a>
                <button className="navbar-toggler ms-auto" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNav">
                    <ul className="navbar-nav me-auto">
                        <li className="nav-item">
                            <Link 
                                className="nav-link" 
                                to="/admin/tournament" 
                                style={{ color: location.pathname === '/admin/tournament' ? 'black' : 'gray' }}
                            >
                                Tournaments
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link 
                                className="nav-link" 
                                to="/admin/ranking" 
                                style={{ color: location.pathname === '/admin/ranking' ? 'black' : 'grey' }}
                            >
                                Ranking
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link 
                                className="nav-link" 
                                to="/admin/playerlist" 
                                style={{ color: location.pathname === '/admin/playerlist' ? 'black' : 'grey' }}
                            >
                                Player List
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link 
                                className="nav-link" 
                                to="/admin/history" 
                                style={{ color: location.pathname === '/admin/history' ? 'black' : 'grey' }}
                            >
                                History
                            </Link>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    );
};

export default AdminNavbar;