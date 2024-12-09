import React, { useEffect, useState } from 'react';

const WatchList = ({ userId, openModal }) => {
  const [watchList, setWatchList] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 20; 
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

  const totalPages = Math.ceil(watchList.length / itemsPerPage);

  const currentResults = watchList.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  return (
    <section id="watchlist">
      <h2>Your Watchlist</h2>
      {errorMessage && <p className="text-danger">{errorMessage}</p>}
      <div id="watchlist-items" className="anime-results">
        {currentResults.length > 0 ? (
          currentResults.map((anime) => (
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

      {/* Pagination Controls */}
      {watchList.length > itemsPerPage && (
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
    </section>
  );
};

export default WatchList;


