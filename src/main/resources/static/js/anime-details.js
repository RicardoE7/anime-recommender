document.addEventListener('DOMContentLoaded', () => {
    // Replace with your method to get the anime ID from the URL or some other source
    const animeId = 113415; // Replace with dynamic ID if needed

    // Fetch anime details from the Spring Boot backend
    fetch(`/anime-details/${animeId}`) // Update to match the endpoint configured in your controller
        .then(response => response.json())
        .then(anime => {
            // Populate HTML elements with anime details
            document.getElementById('coverImage').src = anime.coverImage;
            document.getElementById('title').textContent = anime.title;
            document.getElementById('genres').textContent = anime.genres.join(', ');
            document.getElementById('episodeCount').textContent = anime.episodeCount;
            document.getElementById('description').innerHTML = anime.description;
            document.getElementById('averageScore').textContent = anime.averageScore;
            document.getElementById('popularity').textContent = anime.popularity;
            document.getElementById('releaseDate').textContent = anime.releaseDate;
        })
        .catch(error => {
            console.error('Error fetching anime details:', error);
        });

    document.getElementById('backButton').addEventListener('click', () => {
        window.location.href = '/recommended'; // Replace with the actual URL for your recommendations page
    });
});


