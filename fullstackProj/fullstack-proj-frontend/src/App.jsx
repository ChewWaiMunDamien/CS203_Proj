import { useState } from 'react'
import Navbar from './layout/Navbar.jsx';
import "../node_modules/bootstrap/dist/css/bootstrap.min.css";
import Login from './pages/Login.jsx';
import { BrowserRouter as Router, Route, Routes, useLocation } from "react-router-dom";
import NavbarLogin from './layout/NavbarLogin.jsx';
import Register from './pages/Register.jsx';
import TournamentAdmin from './pages/Admin/TournamentAdmin.jsx';
import TournamentAdminCreate from './pages/Admin/TournamentAdminCreate.jsx';
import TournamentUser from './pages/User/TournamentUser.jsx';
import AdminNavbar from './layout/AdminNavbar.jsx';
import PlayerListAdmin from './pages/Admin/PlayerListAdmin.jsx';
import PlayerListAdminCreate from './pages/Admin/PlayerListAdminCreate.jsx';
import TournamentAdminEdit from './pages/Admin/TournamentAdminEdit.jsx';
import PlayerListAdminEdit from './pages/Admin/PlayerListAdminEdit.jsx';
import TournamentDetailAdmin from './pages/Admin/TournamentDetailAdmin.jsx';
import AddParticpant from './pages/Admin/AddParticpant.jsx';
import RankingAdmin from './pages/Admin/RankingAdmin.jsx';
import TournamentStartAdmin from './pages/Admin/TournamentStartAdmin.jsx';
import RankingUser from './pages/User/RankingUser.jsx';

function App() {

  return (
    <>
      <Router>

        <Routes>
          <Route exact path="/" element={
            <>
              <NavbarLogin></NavbarLogin>
              <Login />
            </>

          }></Route>

          <Route exact path="/register" element={
            <>
              <NavbarLogin></NavbarLogin>
              <Register />
            </>

          }></Route>
          <Route exact path="/admin/tournament" element={
            <>
              <AdminNavbar></AdminNavbar>
              <TournamentAdmin />
            </>
          }></Route>
          <Route exact path="/admin/tournament/create" element={
            <>
              <AdminNavbar></AdminNavbar>
              <TournamentAdminCreate />
            </>
          }></Route>
          <Route exact path="/admin/tournament/edit/:id" element={
            <>
              <AdminNavbar></AdminNavbar>
              <TournamentAdminEdit />
            </>
          }></Route>
          <Route exact path="/admin/tournament/:id" element={
            <>
              <AdminNavbar></AdminNavbar>
              <TournamentDetailAdmin />
            </>
          }></Route>
          <Route exact path="/admin/playerlist" element={
            <>
              <AdminNavbar></AdminNavbar>
              <PlayerListAdmin />
            </>
          }></Route>
          <Route exact path="/admin/playerlist/create" element={
            <>
              <AdminNavbar></AdminNavbar>
              <PlayerListAdminCreate />
            </>
          }></Route>
          <Route exact path="/admin/user/edit/:id" element={
            <>
              <AdminNavbar></AdminNavbar>
              <PlayerListAdminEdit />
            </>
          }></Route>
          <Route exact path="/admin/tournament/:id/add_particpant" element={
            <>
              <AdminNavbar></AdminNavbar>
              <AddParticpant />
            </>
          }></Route>
          <Route exact path="/admin/tournament/:id/start_page" element={
            <>
              <AdminNavbar></AdminNavbar>
              <TournamentStartAdmin />
            </>
          }></Route>
          <Route exact path="/admin/ranking" element={
            <>
              <AdminNavbar></AdminNavbar>
              <RankingAdmin />
            </>
          }></Route>

          <Route exact path="/user/tournament" element={
            <>
              <Navbar></Navbar>
              <TournamentUser />
            </>
          }></Route>

          <Route exact path="/user/ranking" element={
            <>
              <Navbar></Navbar>
              <RankingUser />
            </>
          }></Route>
        </Routes>

      </Router>
    </>

  )
}

export default App
