import React from 'react';

const Header = ({ username }) => (
    <header>
        <h1>Welcome, <span>{username}</span>!</h1>
        <nav>
            <a href="profile.html">Profile</a>
            <a href="logout.html">Logout</a>
        </nav>
    </header>
);

export default Header;
