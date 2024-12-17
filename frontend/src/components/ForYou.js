import React, { useState, useEffect } from 'react';

const ForYou = ({ openModal, userId }) => {
    const [animeList, setAnimeList] = useState([]); // Original data from API
    const [filteredList, setFilteredList] = useState([]); // Filtered data
    const [currentPage, setCurrentPage] = useState(1);
    const [filtersApplied, setFiltersApplied] = useState(false); // Tracks if filters are active
    const itemsPerPage = 20;
    const [errorMessage, setErrorMessage] = useState('');
    const [genres, setGenres] = useState([]);
    const [selectedGenres, setSelectedGenres] = useState(new Set());
    const [showFilterWindow, setShowFilterWindow] = useState(false);

    // Fetch recommendations from the backend
    const fetchRecommendations = async (page) => {
        try {
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
        }
    };

    // Fetch data whenever `userId` or `currentPage` changes
    useEffect(() => {
        if (!filtersApplied) {
            fetchRecommendations(currentPage);
        }
    }, [userId, currentPage, filtersApplied]);

    // Handle genre selection
    const toggleGenreSelection = (genre) => {
        setSelectedGenres((prevSelected) => {
            const updated = new Set(prevSelected);
            if (updated.has(genre)) updated.delete(genre);
            else updated.add(genre);
            return updated;
        });
    };

    // Apply filters
    const applyFilters = () => {
        const filtered = animeList.filter((anime) =>
            [...selectedGenres].every((selectedGenre) => anime.genres?.includes(selectedGenre))
        );
        setFilteredList(filtered);
        setFiltersApplied(true);
        setCurrentPage(1); // Reset pagination
        setShowFilterWindow(false);
    };

    // Clear filters
    const clearFilters = () => {
        setSelectedGenres(new Set());
        setFilteredList(animeList); // Reset to the full list
        setFiltersApplied(false);
        setCurrentPage(1);
    };

    // Pagination logic
    const totalPages = Math.ceil(filteredList.length / itemsPerPage);
    const currentResults = filteredList.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

    return (
        <div>
            <h2>Recommended Anime for You</h2>

            {/* Filter Dropdown */}
            <button onClick={() => setShowFilterWindow(true)}>Filter by Genres</button>

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
                            <button className="watched-btn">Seen It</button>
                            <button className="more-info-btn" onClick={() => openModal(anime)}>More Info</button>
                        </div>
                    ))
                ) : (
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





