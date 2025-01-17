import React, { useState, useEffect } from 'react';

const ForYou = ({ openModal, userId }) => {
    const [animeList, setAnimeList] = useState([]); // Original data from API
    const [filteredList, setFilteredList] = useState([]); // Filtered data
    const [currentPage, setCurrentPage] = useState(1);
    const [filtersApplied, setFiltersApplied] = useState(false); // Tracks if filters are active
    const [isLoading, setIsLoading] = useState(true); // Add loading state
    const itemsPerPage = 20;
    const [errorMessage, setErrorMessage] = useState('');
    const [genres, setGenres] = useState([]);
    const [selectedGenres, setSelectedGenres] = useState(new Set());
    const [showFilterWindow, setShowFilterWindow] = useState(false);

    // Fetch recommendations from the backend
    const fetchRecommendations = async (page) => {
        try {
            setIsLoading(true); // Set loading to true when fetch starts
            const response = await fetch(`http://localhost:8080/recommendations/${userId}?page=${page}`);
            if (!response.ok) throw new Error('Network response was not ok');

            const data = await response.json();
            setAnimeList(data);
            if (!filtersApplied) {
                setFilteredList(data); // Only reset the filtered list when no filters are applied
            }

            // Extract genres
            const allGenres = new Set();
            data.forEach((anime) => anime.genres?.forEach((genre) => allGenres.add(genre)));
            setGenres([...allGenres]);
        } catch (error) {
            console.error('Error fetching recommendations:', error);
            setErrorMessage('Failed to fetch recommendations. Please try again later.');
        } finally {
            setIsLoading(false); // Set loading to false when fetch completes
        }
    };

    // Fetch data whenever `userId` or `currentPage` changes
    useEffect(() => {
        if (!filtersApplied) {
            fetchRecommendations(currentPage);
        }
    }, [userId, currentPage, filtersApplied]);

    
    const toggleGenreSelection = (genre) => {
        setSelectedGenres((prevSelected) => {
            const updated = new Set(prevSelected);
            if (updated.has(genre)) updated.delete(genre);
            else updated.add(genre);
            return updated;
        });
    };

    
    const applyFilters = () => {
        const filtered = animeList.filter((anime) =>
            [...selectedGenres].every((selectedGenre) => anime.genres?.includes(selectedGenre))
        );
        setFilteredList(filtered);
        setFiltersApplied(true);
        setCurrentPage(1); 
        setShowFilterWindow(false);
    };

    
    const clearFilters = () => {
        setSelectedGenres(new Set());
        setFilteredList(animeList); 
        setFiltersApplied(false);
        setCurrentPage(1);
    };

    
    const totalPages = Math.ceil(filteredList.length / itemsPerPage);
    const currentResults = filteredList.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

    return (
        <div>
            <h2>Recommended Anime for You</h2>

            {/* Filter Button Container */}
            <div className="filter-button-container">
                <button onClick={() => setShowFilterWindow(true)}>Filter by Genres</button>
            </div>

            {/* Filter Window */}
            {showFilterWindow && (
                <div className="filter-window">
                    <h3>Select Genres</h3>
                    <div className="genre-checkboxes">
                        {genres.map((genre) => (
                            <label key={genre}>
                                <input
                                    type="checkbox"
                                    checked={selectedGenres.has(genre)}
                                    onChange={() => toggleGenreSelection(genre)}
                                />
                                {genre}
                            </label>
                        ))}
                    </div>
                    <button onClick={applyFilters}>Apply Filters</button>
                    <button onClick={clearFilters}>Clear Filters</button>
                    <button onClick={() => setShowFilterWindow(false)}>Close</button>
                </div>
            )}

            {/* Pagination Controls */}
            {filteredList.length > itemsPerPage && (
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

            <div className="anime-results">
                {errorMessage && <p className="text-danger">{errorMessage}</p>}
                
                {isLoading ? (
                    // Loading spinner or message
                    <div className="loading-container">
                        <div className="loading-spinner"></div>
                        <p>Loading recommendations...</p>
                    </div>
                ) : currentResults.length > 0 ? (
                    // Show results if available
                    currentResults.map((anime) => (
                        <div key={anime.id} className="anime-card">
                            <h3>{anime.title}</h3>
                            <img src={anime.coverImage} alt={`${anime.title} Cover`} className="anime-cover" />
                            <p>{anime.description ? anime.description.substring(0, 100) + '...' : 'No description available'}</p>
                            <p>Episodes: {anime.episodeCount || 'N/A'}</p>
                            <p>Genres: {anime.genres ? anime.genres.join(', ') : 'N/A'}</p>
                            <p>Score: {anime.averageScore || 'N/A'}</p>
                            <button className="watched-btn">Seen It</button>
                            <button className="more-info-btn" onClick={() => openModal(anime)}>More Info</button>
                        </div>
                    ))
                ) : (
                    // Show no results message only if not loading and no results
                    <p>No recommendations available.</p>
                )}
            </div>

            {/* Pagination Controls */}
            {filteredList.length > itemsPerPage && (
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

export default ForYou;





