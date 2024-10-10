import React from 'react';

const HighestRatedList = ({ highestRated, openModal }) => {
  return (
    <section id="highest-rated">
      <h2>Highest Rated Anime</h2>
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
              <button className="details-btn">Details</button>
              <button className="more-info-btn" onClick={() => openModal(anime)}>More Info</button>
            </div>
          );
        })}
      </div>
    </section>
  );
};

export default HighestRatedList;



