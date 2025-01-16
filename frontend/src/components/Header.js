import React, { useState } from 'react';
import logo from '../images/logo.png';
import ProfileModal from './ProfileModal';

const Header = ({ initialUsername, initialEmail }) => {
    const [username, setUsername] = useState(initialUsername || ''); // Default to an empty string if not provided
    const [email, setEmail] = useState(initialEmail || '');
    const [isModalOpen, setIsModalOpen] = useState(false);

    const handleProfileClick = () => {
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
    };

    const handleSaveProfile = (newUsername, newEmail) => {
        if (newUsername) setUsername(newUsername);
        if (newEmail) setEmail(newEmail);
    };

    return (
        <header>
            <img className="logo" src={logo} alt="Logo" />
            <h1>
                Welcome, <span>{username}</span>!
            </h1>
            <nav>
                <a href="#" onClick={handleProfileClick}>
                    Profile
                </a>
                <a href="/login">Logout</a>
            </nav>
            <ProfileModal
                isOpen={isModalOpen}
                onClose={handleCloseModal}
                username={username}
                email={email}
                onSave={handleSaveProfile}
            />
        </header>
    );
};

export default Header;



