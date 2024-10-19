import React, { useState } from 'react';

const HighestRatedList = ({ highestRated, openModal, userId }) => { // Assuming userId is passed as a prop
  const [errorMessage, setErrorMessage] = useState('');

  const handleSeenItClick = async (anime) => {
    // Prompt the user for a rating
    const rating = prompt(`Rate "${anime.title}" from 0 to 100:`);

    // Validate the rating input
    if (rating === null || rating === '') {
      return; // User cancelled the prompt
    }

    const numericRating = Number(rating);
    if (isNaN(numericRating) || numericRating < 0 || numericRating > 100) {
      setErrorMessage('Please enter a valid rating between 0 and 100.');
      return;
    }

    try {
      // Make API call to add the anime to the user's watchlist and save the rating
      const response = await fetch('http://localhost:8080/watchlist', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: userId, // Use the userId prop here
          animeId: anime.id,
          rating: numericRating,
        }),
      });

      if (response.ok) {
        // Handle success (e.g., show a success message or update UI)
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

  return (
    <section id="highest-rated">
      <h2>Top 10 Highest Rated Anime</h2>
      <div id="highest-rated-list" className="anime-scroll">
        {highestRated.map((anime) => {
          console.log('Anime Cover Image:', anime.coverImage);

          return (
            <div key={anime.id} className="anime-item anime-card">
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
          );
        })}
      </div>
    </section>
  );
};

export default HighestRatedList;




