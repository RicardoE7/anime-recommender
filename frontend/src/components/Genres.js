import React, { useEffect, useState } from 'react';

const Genres = () => {
    const [genres, setGenres] = useState([]);

    useEffect(() => {
        const fetchGenres = async () => {
            try {
                const response = await fetch('http://localhost:8080/genres'); // Adjust the port if needed
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                const data = await response.json();
                setGenres(data); // data should be an array of strings based on your API
            } catch (error) {
                console.error('Failed to fetch genres:', error);
            }
        };

        fetchGenres();
    }, []);

    const handleGenreClick = (genre) => {
        // Handle genre click, e.g., navigate to a specific genre page
        console.log(`Genre clicked: ${genre}`);
    };

    return (
        <div>
            <h1>Genres</h1>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
                {genres.map((genre, index) => (
                    <button 
                        key={index} 
                        onClick={() => handleGenreClick(genre)} 
                        style={{ padding: '10px', cursor: 'pointer' }}
                    >
                        {genre} {/* Display genre name */}
                    </button>
                ))}
            </div>
        </div>
    );
};

export default Genres;


