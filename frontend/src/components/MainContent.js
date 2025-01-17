import React, { useEffect, useState } from 'react';
import RecommendationList from './RecommendationList';
import HighestRatedList from './HighestRatedList';
import AnimeModal from './AnimeModal';
import Search from './Search';
import Genres from './Genres'; // Import the Genres component
import GenreResults from './GenreResults'; // Import the GenreResults component
import '../styles/main-content.css';
import WatchList from './WatchList';
import ForYou from './ForYou'


const MainContent = ({ userId }) => {
	const [recommendations, setRecommendations] = useState([]);
	const [highestRated, setHighestRated] = useState([]);
	const [genres, setGenres] = useState([]); // State to hold genres
	const [selectedGenre, setSelectedGenre] = useState(null); // Track the selected genre filter
	const [modalData, setModalData] = useState(null);
	const [activeView, setActiveView] = useState('home');

	const fetchData = async () => {
		try {
			// Fetch recommended anime
			const recommendedResponse = await fetch(`http://localhost:8080/recommended-anime/${userId}`);
			const recommendedData = await recommendedResponse.json();
			setRecommendations(recommendedData);

			// Fetch highest rated anime
			const highestRatedResponse = await fetch(`http://localhost:8080/top-anime/${userId}`);
			const highestRatedData = await highestRatedResponse.json();
			setHighestRated(highestRatedData);

			// Fetch genres
			const genresResponse = await fetch('http://localhost:8080/genres');
			const genresData = await genresResponse.json();
			setGenres(Array.isArray(genresData) ? genresData : []); // Ensure genresData is an array
		} catch (error) {
			console.error("Error fetching data:", error);
		}
	};


	const refreshData = () => {
		fetchData(); // Re-fetch all data
	};

	useEffect(() => {
		fetchData(); // Fetch data on mount
	}, [userId]);

	// Handle genre selection
	const handleGenreSelect = (genre) => {
		setSelectedGenre(genre);
		setActiveView('genre'); // Set active view to genre results
	};

	const clearGenreFilter = () => {
		setSelectedGenre(null);
		setActiveView('home'); // Return to home view when clearing the filter
	};

	const openModal = (anime) => {
		setModalData(anime);
	};

	return (
		<div className="main-layout">
			{/* Sidebar */}
			<aside className="sidebar">
				<ul>
					<li><button onClick={() => setActiveView('home')}>Home</button></li>
					<li><button onClick={() => setActiveView('watchlist')}>My Watchlist</button></li>
					<li><button onClick={() => setActiveView('foryou')}>For You</button></li>
					<li><button onClick={() => setActiveView('topAnime')}>By Rating</button></li>
					<li><button onClick={() => setActiveView('recommendations')}>Most Popular</button></li>
					<li><button onClick={() => setActiveView('search')}>Search</button></li>
				</ul>
			</aside>

			{/* Main Content */}
			<main className="content">
				{activeView === 'search' ? (
					<Search openModal={openModal} userId={userId} />
				) : (
					<>
						{activeView === 'home' && (
							<>
								<RecommendationList recommendations={recommendations} openModal={openModal} userId={userId} refreshData={refreshData} />
								<HighestRatedList highestRated={highestRated} openModal={openModal} userId={userId} refreshData={refreshData} />
							</>
						)}
						{activeView === 'watchlist' && (
							<WatchList openModal={openModal} userId={userId} />
						)}
						{activeView === 'foryou' && (
							<ForYou openModal={openModal} userId={userId} />
						)}
						{activeView === 'recommendations' && (
							<RecommendationList recommendations={recommendations} openModal={openModal} userId={userId} />
						)}
						{activeView === 'topAnime' && (
							<HighestRatedList highestRated={highestRated} openModal={openModal} userId={userId} />
						)}
						{activeView === 'genre' && selectedGenre && (
							<GenreResults openModal={openModal} userId={userId} genre={selectedGenre} clearFilter={clearGenreFilter} />
						)}
					</>
				)}

				{/* Render AnimeModal if modalData is set */}
				{modalData && (
					<AnimeModal
						title={modalData.title}
						coverImage={modalData.coverImage}
						description={modalData.description}
						episodes={modalData.episodeCount}
						genres={modalData.genres}
						averageScore={modalData.averageScore}
						onClose={() => setModalData(null)}
					/>
				)}
			</main>
		</div>
	);
};

export default MainContent;













