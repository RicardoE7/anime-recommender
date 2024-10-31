import React, { useState, useEffect } from 'react';

const GenreResults = ({ openModal, userId, genre, clearFilter }) => {
    const [animeList, setAnimeList] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 20; // Number of items per page
    const [errorMessage, setErrorMessage] = useState('');
	
	const handleSeenItClick = async (anime) => {
        const rating = prompt(`Rate "${anime.title}" from 0 to 100:`);

        if (rating === null || rating === '') return;

        const numericRating = Number(rating);
        if (isNaN(numericRating) || numericRating < 0 || numericRating > 100) {
            setErrorMessage('Please enter a valid rating between 0 and 100.');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/watchlist', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    userId: userId,
                    animeId: anime.id,
                    rating: numericRating,
                }),
            });

            if (response.ok) {
                alert(`"${anime.title}" added to your watchlist with a rating of ${numericRating}.`);
            } else {
                const errorData = await response.json();
                setErrorMessage(errorData.message || 'Failed to add to watchlist. Please try again.');
            }
        } catch (error) {
            console.error('Error adding to watchlist:', error);
            setErrorMessage('An error occurred while adding to watchlist. Please try again.');
        }
    };

	
    useEffect(() => {
        const fetchGenreAnime = async () => {
            try {
                const response = await fetch(`http://localhost:8080/genre/${encodeURIComponent(genre)}?page=${currentPage}`);
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                const data = await response.json();
                setAnimeList(data);
            } catch (error) {
                console.error("Error fetching genre anime:", error);
                setErrorMessage('Failed to fetch anime of this genre. Please try again later.');
            }
        };

        fetchGenreAnime();
    }, [genre, currentPage]);

    // Calculate total pages
    const totalPages = Math.ceil(animeList.length / itemsPerPage);

    // Get the results for the current page
    const currentResults = animeList.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

    return (
        <div>
            <h2>Anime in Genre: {genre}</h2>
            <div className="anime-results">
                {errorMessage && <p className="text-danger">{errorMessage}</p>}
                {currentResults.length > 0 ? (
                    currentResults.map((anime) => (
                        <div key={anime.id} className="anime-card">
                            <h3>{anime.title}</h3>
                            <img src={anime.coverImage} alt={`${anime.title} Cover`} className="anime-cover" />
                            <p>{anime.description ? anime.description.substring(0, 100) + '...' : 'No description available'}</p>
                            <p>Episodes: {anime.episodeCount || 'N/A'}</p>
                            <p>Genres: {anime.genres ? anime.genres.join(', ') : 'N/A'}</p>
                            <p>Score: {anime.averageScore || 'N/A'}</p>
                            <button className="watched-btn" onClick={() => handleSeenItClick(anime)}>Seen It</button>
                            <button className="more-info-btn" onClick={() => openModal(anime)}>More Info</button>
                            {errorMessage && <p className="text-danger">{errorMessage}</p>}
                        </div>
                    ))
                ) : (
                    <p>No anime found for this genre.</p>
                )}
            </div>

            {/* Pagination Controls */}
            {animeList.length > itemsPerPage && (
                <div className="pagination">
                    <button
                        onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
                        disabled={currentPage === 1}
                    >
                        Previous Page
                    </button>
                    <span>Page {currentPage} of {totalPages}</span>
                    <button
                        onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
                        disabled={currentPage === totalPages}
                    >
                        Next Page
                    </button>
                </div>
            )}
        </div>
    );
};

export default GenreResults;

