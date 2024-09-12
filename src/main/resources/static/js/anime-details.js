document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const animeId = urlParams.get('id');

    if (animeId) {
        const query = `
        {
            Media(id: ${animeId}) {
                title {
                    romaji
                    english
                }
                episodes
                genres
                coverImage {
                    large
                }
                description
                averageScore
            }
        }`;

        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({ query })
        };

        try {
            const response = await fetch('https://graphql.anilist.co/', options);
            const data = await response.json();
            const anime = data.data.Media;

            // Populate the page with anime details
            document.getElementById('anime-title').textContent = anime.title.english || anime.title.romaji;
            document.getElementById('anime-cover').src = anime.coverImage.large;
            document.getElementById('anime-description').textContent = anime.description.replace(/<br\s*\/?>/gi, '\n') || 'No description available';
            document.getElementById('anime-episodes').textContent = `Episodes: ${anime.episodes}`;
            document.getElementById('anime-genres').textContent = `Genres: ${anime.genres.join(', ')}`;
            document.getElementById('anime-score').textContent = `Score: ${anime.averageScore}`;
        } catch (error) {
            console.error('Error fetching anime details:', error);
        }
    }
});
