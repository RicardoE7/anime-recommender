document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const animeId = urlParams.get('id');

    if (animeId) {
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            }
        };

        try {
            // Replace the URL with your backend API endpoint
            const response = await fetch(`/anime-details/${animeId}`, options);
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const anime = await response.json();

            // Populate the page with anime details from your backend
            document.getElementById('anime-title').textContent = anime.title;
            document.getElementById('anime-cover').src = anime.coverImage;
            document.getElementById('anime-description').textContent = anime.description.replace(/<br\s*\/?>/gi, '\n') || 'No description available';
            document.getElementById('anime-episodes').textContent = `Episodes: ${anime.episodeCount}`;
            document.getElementById('anime-genres').textContent = `Genres: ${anime.genres.join(', ')}`;
            document.getElementById('anime-score').textContent = `Score: ${anime.averageScore}`;
        } catch (error) {
            console.error('Error fetching anime details:', error);
        }
    }
});

