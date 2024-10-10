import React from 'react';

const AnimeModal = ({ title, coverImage, description, episodes, genres, averageScore, onClose }) => (
    <div className="modal" style={{ display: 'block' }}> {/* You might want to handle the display dynamically */}
        <div className="modal-content">
            <span className="close" onClick={onClose}>&times;</span>
            <h2>{title}</h2>
            <img 
                src={coverImage} 
                alt={`${title} Cover Image`} 
                className="anime-cover" 
                style={{ width: '150px', height: '225px', objectFit: 'cover', marginBottom: '10px' }} 
            />
            <p>{description ? description : 'No description available'}</p>
            <p>Episodes: {episodes || 'N/A'}</p>
            <p>Genres: {genres && genres.length > 0 ? genres.join(', ') : 'N/A'}</p>
            <p>Score: {averageScore || 'N/A'}</p>
        </div>
    </div>
);

export default AnimeModal;

