import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import './styles/styles.css';
import Header from './components/Header';
import MainContent from './components/MainContent';
import ScoreLinks from './components/ScoreLinks';
import Footer from './components/Footer';
import AnimeModal from './components/AnimeModal';
import Login from './components/Login';
import Register from './components/Register'; // Import the Register component
import Search from './components/Search'; // Import the Search component
import Genres from './components/Genres'; // Import the Genres component
import GenreResults from './components/GenreResults'; // Import the GenreResults component

const App = () => {
    const [username, setUsername] = React.useState(null);
    const [userId, setUserId] = React.useState(null); // State for user ID
    const [modalData, setModalData] = React.useState(null);

    const handleLogin = (user) => {
        setUsername(user.username); // Set the username
        setUserId(user.id); // Set the user ID
    };

    return (
        <Router>
            <div className="App">
                <Routes>
                    <Route path="/login" element={<Login onLogin={handleLogin} />} />
                    <Route path="/register" element={<Register />} /> {/* Add Register Route */}
                    <Route path="/search" element={<Search />} /> {/* Add Search Route */}
                    <Route path="/genres" element={<Genres />} /> {/* Route for Genres */}
                    <Route path="/genre/:genre" element={<GenreResults />} /> {/* Route for GenreResults */}
                    <Route path="/" element={username ? (
                        <>
                            <Header username={username} />
                            <MainContent userId={userId} /> {/* Pass userId to MainContent */}
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
                    )} />
                </Routes>
            </div>
        </Router>
    );
};

export default App;






