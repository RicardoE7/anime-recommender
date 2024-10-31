import React, { useEffect, useState } from 'react';
import RecommendationList from './RecommendationList';
import HighestRatedList from './HighestRatedList';
import AnimeModal from './AnimeModal';
import Search from './Search';
import Genres from './Genres'; // Import the Genres component
import '../styles/main-content.css';

const MainContent = ({ userId }) => {
    const [recommendations, setRecommendations] = useState([]);
    const [highestRated, setHighestRated] = useState([]);
    const [genres, setGenres] = useState([]); // State to hold genres
    const [selectedGenre, setSelectedGenre] = useState(null); // Track the selected genre filter
    const [modalData, setModalData] = useState(null);
    const [activeView, setActiveView] = useState('home');

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

                // Fetch genres
                const genresResponse = await fetch('http://localhost:8080/genres');
                const genresData = await genresResponse.json();
                setGenres(Array.isArray(genresData) ? genresData : []); // Ensure genresData is an array
            } catch (error) {
                console.error("Error fetching data:", error);
            }
        };

        fetchData();
    }, []);

    // Filter recommendations and highest-rated lists based on the selected genre
    const filteredRecommendations = selectedGenre
        ? recommendations.filter(anime => anime.genres.includes(selectedGenre))
        : recommendations;

    const filteredHighestRated = selectedGenre
        ? highestRated.filter(anime => anime.genres.includes(selectedGenre))
        : highestRated;

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
                <Genres
                    genres={genres}
                    selectedGenre={selectedGenre}
                    onGenreSelect={setSelectedGenre} // Pass the setter function to handle genre selection
                    clearFilter={() => setSelectedGenre(null)} // Clear genre filter when needed
                />
            </aside>

            {/* Main Content */}
            <main className="content">
                {activeView === 'search' ? (
                    <Search openModal={openModal} userId={userId} />
                ) : (
                    <>
                        {activeView === 'home' && (
                            <>
                                <RecommendationList recommendations={filteredRecommendations} openModal={openModal} userId={userId} />
                                <HighestRatedList highestRated={filteredHighestRated} openModal={openModal} userId={userId} />
                            </>
                        )}
                        {activeView === 'recommendations' && (
                            <RecommendationList recommendations={filteredRecommendations} openModal={openModal} userId={userId} />
                        )}
                        {activeView === 'topAnime' && (
                            <HighestRatedList highestRated={filteredHighestRated} openModal={openModal} userId={userId} />
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
                        onClose={() => setModalData(null)}
                    />
                )}
            </main>
        </div>
    );
};

export default MainContent;









