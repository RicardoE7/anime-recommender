async function fetchAnimeRecommendations() {
    const query = `
    {
        Page(page: 1, perPage: 10) {
            media(type: ANIME, sort: POPULARITY_DESC) {
                id
                title {
                    romaji
                }
                episodes
                genres
                coverImage {
                    large
                }
                description
            }
        }
    }`;

    const url = 'https://graphql.anilist.co';

    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        body: JSON.stringify({
            query: query
        })
    };

    try {
        const response = await fetch(url, options);
        const data = await response.json();
        const animeList = data.data.Page.media;
        displayAnime(animeList);
    } catch (error) {
        console.error('Error fetching anime recommendations:', error);
    }
}

function displayAnime(animeList) {
    const animeListDiv = document.getElementById('anime-list');

    animeList.forEach(anime => {
        const animeCard = `
            <div class="anime-card">
                <h3>${anime.title.romaji}</h3>
                <img src="${anime.coverImage.large}" alt="${anime.title.romaji} Cover Image" class="anime-cover">
                <p>${anime.description ? anime.description.substring(0, 100) + '...' : 'No description available'}</p>
                <p>Episodes: ${anime.episodes}</p>
                <p>Genres: ${anime.genres.join(', ')}</p>
                <button onclick="showAnimeDetails('${anime.title.romaji}', '${anime.description}')">Details</button>
            </div>`;
        animeListDiv.innerHTML += animeCard;
    });
}

function showAnimeDetails(title, description) {
    const modal = document.getElementById('anime-modal');
    const modalTitle = document.getElementById('anime-title');
    const modalDescription = document.getElementById('anime-description');

    modalTitle.textContent = title;
    modalDescription.textContent = description || 'No description available';

    modal.style.display = 'block';

    // Close the modal
    const closeModal = document.getElementsByClassName('close')[0];
    closeModal.onclick = function () {
        modal.style.display = 'none';
    }

    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    }
}

// Fetch anime recommendations when the page loads
fetchAnimeRecommendations();
