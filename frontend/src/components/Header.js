import React from 'react';

const Header = ({ username }) => (
    <header>
        <h1>Welcome, <span>{username}</span>!</h1>
        <nav>
            <a href="/profile">Profile</a>
            <a href="/login">Logout</a>
        </nav>
    </header>
);

export default Header;
