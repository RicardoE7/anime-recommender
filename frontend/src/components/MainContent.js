import React, { useEffect, useState } from 'react';
import RecommendationList from './RecommendationList';
import HighestRatedList from './HighestRatedList';
import AnimeModal from './AnimeModal'; // Import the AnimeModal component

const MainContent = () => {
    const [recommendations, setRecommendations] = useState([]);
    const [highestRated, setHighestRated] = useState([]);
    const [modalData, setModalData] = useState(null); // Manage modal state

    useEffect(() => {
        const fetchData = async () => {
            try {
                // Fetch recommended anime
                const recommendedResponse = await fetch('http://localhost:8080/recommended-anime'); // Adjust this endpoint
                const recommendedData = await recommendedResponse.json();
                setRecommendations(recommendedData);

                // Fetch highest rated anime
                const highestRatedResponse = await fetch('http://localhost:8080/top-anime'); // Adjust this endpoint
                const highestRatedData = await highestRatedResponse.json();
                setHighestRated(highestRatedData);
            } catch (error) {
                console.error("Error fetching anime data:", error);
            }
        };

        fetchData();
    }, []);

    // Function to handle setting modal data
    const openModal = (anime) => {
        setModalData(anime);
    };

    return (
        <main>
            <RecommendationList recommendations={recommendations} openModal={openModal} />
            <HighestRatedList highestRated={highestRated} openModal={openModal} />
            
            {/* Render AnimeModal if modalData is set */}
            {modalData && (
                <AnimeModal
                    title={modalData.title}
                    coverImage={modalData.coverImage}
                    description={modalData.description}
                    episodes={modalData.episodeCount}
                    genres={modalData.genres}
                    averageScore={modalData.averageScore}
                    onClose={() => setModalData(null)} // Close the modal
                />
            )}
        </main>
    );
};

export default MainContent;


