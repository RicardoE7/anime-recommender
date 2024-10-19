import React, { useEffect, useState } from 'react';
import RecommendationList from './RecommendationList';
import HighestRatedList from './HighestRatedList';
import AnimeModal from './AnimeModal';
import Search from './Search'; // Import the Search component
import '../styles/main-content.css';

const MainContent = ({ userId }) => {
    const [recommendations, setRecommendations] = useState([]);
    const [highestRated, setHighestRated] = useState([]);
    const [modalData, setModalData] = useState(null); // Manage modal state
    const [activeView, setActiveView] = useState('home'); // Track the active view, default to 'home'

    useEffect(() => {
        const fetchData = async () => {
            try {
                // Fetch recommended anime
                const recommendedResponse = await fetch('http://localhost:8080/recommended-anime');
                const recommendedData = await recommendedResponse.json();
                setRecommendations(recommendedData);

                // Fetch highest rated anime
                const highestRatedResponse = await fetch('http://localhost:8080/top-anime');
                const highestRatedData = await highestRatedResponse.json();
                setHighestRated(highestRatedData);
            } catch (error) {
                console.error("Error fetching anime data:", error);
            }
        };

        fetchData();
    }, []);

    // Function to handle setting modal data
    const openModal = (anime) => {
        setModalData(anime);
    };

    return (
        <div className="main-layout">
            {/* Sidebar */}
            <aside className="sidebar">
                <ul>
                    <li><button onClick={() => setActiveView('home')}>Home</button></li>
                    <li><button onClick={() => setActiveView('watchlist')}>My Watchlist</button></li>
                    <li><button onClick={() => setActiveView('topAnime')}>By Rating</button></li>
                    <li><button onClick={() => setActiveView('recommendations')}>Most Popular</button></li>
                    <li><button onClick={() => setActiveView('search')}>Search</button></li>
                </ul>
            </aside>

            {/* Main Content */}
            <main className="content">
                {activeView === 'search' ? (
                    <Search openModal={openModal} userId={userId} /> 
                ) : (
                    <>
                        {activeView === 'home' && (
                            <>
                                {/* Render both lists for the "home" view */}
                                <RecommendationList recommendations={recommendations} openModal={openModal} userId={userId} />
                                <HighestRatedList highestRated={highestRated} openModal={openModal} userId={userId} />
                            </>
                        )}
                        {activeView === 'recommendations' && (
                            <RecommendationList recommendations={recommendations} openModal={openModal} userId={userId} />
                        )}
                        {activeView === 'topAnime' && (
                            <HighestRatedList highestRated={highestRated} openModal={openModal} userId={userId} />
                        )}
                    </>
                )}

                {/* Render AnimeModal if modalData is set */}
                {modalData && (
                    <AnimeModal
                        title={modalData.title}
                        coverImage={modalData.coverImage}
                        description={modalData.description}
                        episodes={modalData.episodeCount}
                        genres={modalData.genres}
                        averageScore={modalData.averageScore}
                        onClose={() => setModalData(null)} // Close the modal
                    />
                )}
            </main>
        </div>
    );
};

export default MainContent;







