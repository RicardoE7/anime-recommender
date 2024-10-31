import React, { useEffect, useState } from 'react';

const Genres = ({ genres, selectedGenre, onGenreSelect, clearFilter }) => {
    // You no longer need to fetch genres here since you're passing them as props
    // If you still need to fetch genres here, remove the genres prop from MainContent
    // and implement fetching logic in this component

    return (
        <div>
            <h1>Genres</h1>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
                {genres.map((genre, index) => (
                    <button 
                        key={index} 
                        onClick={() => onGenreSelect(genre)} // Call onGenreSelect to update state in MainContent
                        style={{ padding: '10px', cursor: 'pointer' }}
                    >
                        {genre} {/* Display genre name */}
                    </button>
                ))}
                <button onClick={clearFilter} style={{ padding: '10px', cursor: 'pointer' }}>
                    Clear Filter
                </button>
            </div>
        </div>
    );
};

export default Genres;




