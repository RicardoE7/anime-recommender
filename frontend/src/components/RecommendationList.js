import React, { useState } from 'react';

const RecommendationList = ({ recommendations, openModal, userId, refreshData }) => {
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
        refreshData();
      } else {
        const errorData = await response.json();
        setErrorMessage(errorData.message || 'Failed to add to watchlist. Please try again.');
      }
    } catch (error) {
      console.error('Error adding to watchlist:', error);
      setErrorMessage('An error occurred while adding to watchlist. Please try again.');
    }
  };

  // Fallback check for recommendations
  if (!Array.isArray(recommendations)) {
    console.error('Invalid recommendations prop:', recommendations);
    return <p>No recommendations available.</p>;
  }

  return (
    <section id="most-popular">
      <h2>Top 10 Most Popular Anime</h2>
      <div id="most-popular-list" className="anime-scroll">
        {recommendations.map((anime) => (
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
        ))}
      </div>
    </section>
  );
};

export default RecommendationList;


