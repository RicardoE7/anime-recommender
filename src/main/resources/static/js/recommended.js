const apiUrl = 'https://graphql.anilist.co/';

// Fetch anime recommendations from AniList API
async function fetchAnimeRecommendations() {
    const query = `
    {
        Page(page: 1, perPage: 10) {
            media(type: ANIME, sort: POPULARITY_DESC) {
                id
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
        const response = await fetch(apiUrl, options);
        const text = await response.text(); // Read response as text
        console.log('Recommendations Response text:', text); // Log response text for debugging

        const data = JSON.parse(text); // Parse the text to JSON
        const animeList = data.data.Page.media;
        console.log('Recommendations Data:', animeList); // Log data for debugging
        displayAnime(animeList, 'anime-list');
    } catch (error) {
        console.error('Error fetching anime recommendations:', error);
    }
}

// Fetch highest rated anime from AniList API
async function fetchHighestRatedAnime() {
    const query = `
    {
        Page(page: 1, perPage: 10) {
            media(type: ANIME, sort: SCORE_DESC) {
                id
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
        const response = await fetch(apiUrl, options);
        const text = await response.text(); // Read response as text
        console.log('Highest Rated Response text:', text); // Log response text for debugging

        const data = JSON.parse(text); // Parse the text to JSON
        const highestRatedList = data.data.Page.media;
        console.log('Highest Rated Data:', highestRatedList); // Log data for debugging
        displayAnime(highestRatedList, 'highest-rated-list');
    } catch (error) {
        console.error('Error fetching highest rated anime:', error);
    }
}

// Fetch single anime by ID from AniList API
async function fetchAnimeById(animeId) {
    console.log('Fetching anime with ID:', animeId); // Log the anime ID
    const query = `
    query ($id: Int) {
        Media(id: $id, type: ANIME) {
            id
            title {
                romaji
                english
                native
            }
            genres
            episodes
            coverImage{
                large
            }
            description
            averageScore
            popularity
            startDate{
                year
                month
                day
            }
        }
    }`;

    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        body: JSON.stringify({
            query,
            variables: { id: animeId } // Pass the variable here
        })
    };

    try {
        const response = await fetch(apiUrl, options);
        const text = await response.text(); // Read response as text
        console.log('Fetch by ID Response text:', text); // Log response text for debugging

        const data = JSON.parse(text); // Parse the text to JSON
        if (data.data && data.data.Media) {
            return data.data.Media;
        } else {
            console.error('No data found for anime ID:', animeId);
            return null;
        }
    } catch (error) {
        console.error('Error fetching anime by ID:', error);
        return null;
    }
}

// Display anime list in the specified container
function displayAnime(animeList, containerId) {
    const animeListDiv = document.getElementById(containerId);
    if (!animeListDiv) {
        console.error('Container not found:', containerId);
        return;
    }

    animeListDiv.innerHTML = ''; // Clear previous content

    animeList.forEach(anime => {
        // Check if required fields exist
        if (!anime.title || !anime.title.english && !anime.title.romaji) {
            console.warn('Anime data missing title:', anime);
            return;
        }

        // Use English title if available, otherwise fallback to Romaji
        const title = anime.title.english || anime.title.romaji || 'No title available';

        // Create anime card HTML
        const animeCard = document.createElement('div');
        animeCard.classList.add('anime-card');

        // Set the inner HTML of the anime card
        animeCard.innerHTML = `
            <h3>${title}</h3>
            <img src="${anime.coverImage ? anime.coverImage.large : ''}" alt="${title} Cover Image" class="anime-cover">
            <p>${anime.description ? anime.description.substring(0, 100) + '...' : 'No description available'}</p>
            <p>Episodes: ${anime.episodes || 'N/A'}</p>
            <p>Genres: ${anime.genres ? anime.genres.join(', ') : 'N/A'}</p>
            <p>Score: ${anime.averageScore || 'N/A'}</p>
            <button class="details-btn">Details</button>
            <button class="more-info-btn" data-id="${anime.id}">More Info</button>
        `;

        // Append the anime card to the list
        animeListDiv.appendChild(animeCard);

        // Attach event listener to the "Details" button
        const detailsButton = animeCard.querySelector('.details-btn');
        detailsButton.addEventListener('click', () => {
            showAnimeDetails(title, anime.description);
        });

        // Attach event listener to the "More Info" button
        const moreInfoButton = animeCard.querySelector('.more-info-btn');
        moreInfoButton.addEventListener('click', async () => {
            const animeId = moreInfoButton.getAttribute('data-id');
            console.log('More Info clicked, animeId:', animeId); // Log the clicked anime ID
            const anime = await fetchAnimeById(animeId);
            if (anime) {
                window.location.href = `/anime-details/${animeId}`;
            }
        });
    });
}

// Show anime details in a modal
function showAnimeDetails(title, description) {
    const modal = document.getElementById('anime-modal');
    const modalTitle = document.getElementById('anime-title');
    const modalDescription = document.getElementById('anime-description');

    // Clean the description for modal, replacing <br> with new lines
    const cleanedDescription = description ? description.replace(/<br\s*\/?>/gi, '\n') : 'No description available';

    modalTitle.textContent = title;
    modalDescription.textContent = cleanedDescription;

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

// Function to enable dragging for scrolling
function enableDragScrolling(container) {
    let isDown = false;
    let startX;
    let scrollLeft;

    container.addEventListener('mousedown', (e) => {
        isDown = true;
        container.classList.add('active');
        startX = e.pageX - container.offsetLeft;
        scrollLeft = container.scrollLeft;
    });

    container.addEventListener('mouseleave', () => {
        isDown = false;
        container.classList.remove('active');
    });

    container.addEventListener('mouseup', () => {
        isDown = false;
        container.classList.remove('active');
    });

    container.addEventListener('mousemove', (e) => {
        if (!isDown) return;
        e.preventDefault();
        const x = e.pageX - container.offsetLeft;
        const walk = (x - startX) * 2; // Scroll-fast factor
        container.scrollLeft = scrollLeft - walk;
    });
}

// Fetch anime recommendations and highest rated anime when the page loads
fetchAnimeRecommendations();
fetchHighestRatedAnime();

// Enable drag scrolling for anime lists
enableDragScrolling(document.getElementById('anime-list'));
enableDragScrolling(document.getElementById('highest-rated-list'));
