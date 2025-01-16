import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import './styles/styles.css';
import Header from './components/Header';
import MainContent from './components/MainContent';
import ScoreLinks from './components/ScoreLinks';
import Footer from './components/Footer';
import AnimeModal from './components/AnimeModal';
import Login from './components/Login';
import Register from './components/Register';
import Search from './components/Search';
import Genres from './components/Genres';
import GenreResults from './components/GenreResults';
import Video from './images/videoplayback.mp4';

const App = () => {
    const [username, setUsername] = useState(null);
    const [email, setEmail] = useState(null);
    const [userId, setUserId] = useState(null);
    const [modalData, setModalData] = useState(null);
    const [isLoading, setIsLoading] = useState(true); // State to handle loading

    // Restore user data from localStorage on initial load
    useEffect(() => {
        const storedUsername = localStorage.getItem('username');
        const storedEmail = localStorage.getItem('email');
        const storedUserId = localStorage.getItem('userId');

        if (storedUsername && storedEmail && storedUserId) {
            setUsername(storedUsername);
            setEmail(storedEmail);
            setUserId(storedUserId);
        }

        setIsLoading(false); // Loading complete
    }, []);

    // Handle user login
    const handleLogin = (user) => {
        setUsername(user.username);
        setEmail(user.email);
        setUserId(user.id);

        // Save user data to localStorage
        localStorage.setItem('username', user.username);
        localStorage.setItem('email', user.email);
        localStorage.setItem('userId', user.id);
    };

    // Handle user logout
    const handleLogout = () => {
        setUsername(null);
        setEmail(null);
        setUserId(null);

        // Clear user data from localStorage
        localStorage.removeItem('username');
        localStorage.removeItem('email');
        localStorage.removeItem('userId');
    };

    // Show a loading indicator while checking for stored user data
    if (isLoading) {
        return <div>Loading...</div>;
    }

    return (
        <Router>
            <div className="App">
                <video src={Video} autoPlay loop muted />
                <Routes>
                    <Route
                        path="/login"
                        element={<Login onLogin={handleLogin} />}
                    />
                    <Route path="/register" element={<Register />} />
                    <Route path="/search" element={<Search />} />
                    <Route path="/genres" element={<Genres />} />
                    <Route path="/genre/:genre" element={<GenreResults />} />
                    <Route
                        path="/"
                        element={
                            username ? (
                                <>
                                    <Header
                                        initialUsername={username}
                                        initialEmail={email}
                                        onLogout={handleLogout}
                                    />
                                    <MainContent userId={userId} />
                                    <Footer />
                                    {modalData && (
                                        <AnimeModal
                                            title={modalData.title}
                                            description={modalData.description}
                                            onClose={() => setModalData(null)}
                                        />
                                    )}
                                </>
                            ) : (
                                <Navigate to="/login" />
                            )
                        }
                    />
                </Routes>
            </div>
        </Router>
    );
};

export default App;










