import React from 'react';
import logo from '../images/logo.png'

const Header = ({ username }) => (
    <header>
    	<img className='logo' src={logo}></img>
        <h1>Welcome, <span>{username}</span>!</h1>
        <nav>
            <a href="/profile">Profile</a>
            <a href="/login">Logout</a>
        </nav>
    </header>
);

export default Header;
