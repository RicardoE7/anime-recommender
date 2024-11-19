import React, { useEffect, useState } from 'react';

const WatchList = ({ userId, openModal }) => {
  const [watchList, setWatchList] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    const fetchWatchList = async () => {
      try {
        const response = await fetch(`http://localhost:8080/${userId}/watchlist`);
        if (!response.ok) {
          throw new Error('Failed to fetch watchlist');
        }
        const data = await response.json();
        setWatchList(data);
      } catch (error) {
        console.error('Error fetching watchlist:', error);
        setErrorMessage('An error occurred while fetching your watchlist. Please try again later.');
      }
    };

    fetchWatchList();
  }, [userId]);

  return (
    <section id="watchlist">
      <h2>Your Watchlist</h2>
      {errorMessage && <p className="text-danger">{errorMessage}</p>}
      <div id="watchlist-items" className="anime-scroll">
        {watchList.length > 0 ? (
          watchList.map((anime) => (
            <div key={anime.id} className="anime-card">
              <h3>{anime.title}</h3>
              <img src={anime.coverImage} alt={`${anime.title} Cover`} className="anime-cover" />
              <p>{anime.description ? anime.description.substring(0, 100) + '...' : 'No description available'}</p>
              <p>Episodes: {anime.episodeCount || 'N/A'}</p>
              <p>Genres: {anime.genres ? anime.genres.join(', ') : 'N/A'}</p>
              <p>Score: {anime.averageScore || 'N/A'}</p>
              <p>Your Score: {anime.userRating !== null ? anime.userRating : 'Not rated yet'}</p>
              <button className="more-info-btn" onClick={() => openModal(anime)}>More Info</button>
            </div>
          ))
        ) : (
          <p>Your watchlist is empty.</p>
        )}
      </div>
    </section>
  );
};

export default WatchList;
