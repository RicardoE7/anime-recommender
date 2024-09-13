package com.watchmoreanime.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.watchmoreanime.domain.Anime;
import com.watchmoreanime.repository.AnimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnimeService {

    @Autowired
    private AnimeRepository animeRepository;

    private final String apiUrl = "https://graphql.anilist.co/";

    // Fetch Anime by ID from your local database
    public Anime getAnimeById(Long id) {
        return animeRepository.findById(id).orElse(null);
    }

    // Fetch Anime from AniList API by Anime ID
    public Anime fetchAnimeFromApi(Long animeId) {
        // Correct GraphQL query format with variable $id
        String query = """
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
                coverImage {
                    large
                }
                description
                averageScore
                popularity
                startDate {
                    year
                    month
                    day
                }
            }
        }
        """;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Escape special characters in the query string for JSON
        String formattedQuery = query.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"");
        String jsonRequest = String.format("{\"query\":\"%s\",\"variables\":{\"id\":%d}}", formattedQuery, animeId);
        HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
            String response = responseEntity.getBody();

            // Parse the response into an Anime object
            return parseApiResponse(response);

        } catch (Exception e) {
            System.err.println("Error during API call: " + e.getMessage());
            e.printStackTrace();
            return null; // Handle the error appropriately
        }
    }

    // Saving anime to the database with genres directly as strings
    @Transactional
    public void saveAnimeWithGenres(Anime anime, List<String> genreNames) {
        // Set genres directly as strings
        anime.setGenres(genreNames);

        // Save the anime to the database
        animeRepository.save(anime);
    }

    // Function to parse API response to Anime object
    public Anime parseApiResponse(String response) {
        ObjectMapper mapper = new ObjectMapper();
        Anime anime = new Anime();

        try {
            JsonNode root = mapper.readTree(response).path("data").path("Media");

            // Set anime ID
            anime.setId(root.path("id").asLong());

            // Set anime title (use English if available, otherwise Romaji)
            JsonNode titleNode = root.path("title");
            if (titleNode.has("english") && !titleNode.path("english").isMissingNode()) {
                anime.setTitle(titleNode.path("english").asText());
            } else {
                anime.setTitle(titleNode.path("romaji").asText());
            }

            // Set genres as a list of strings
            List<String> genres = new ArrayList<>();
            JsonNode genresNode = root.path("genres");
            if (genresNode.isArray()) {
                for (JsonNode genreNode : genresNode) {
                    genres.add(genreNode.asText());
                }
            }
            anime.setGenres(genres);

            // Set episode count
            anime.setEpisodeCount(root.path("episodes").asInt());

            // Set cover image URL
            anime.setCoverImage(root.path("coverImage").path("large").asText());

            // Set description
            anime.setDescription(root.path("description").asText());

            // Set average score
            anime.setAverageScore(root.path("averageScore").asInt());

            // Set popularity
            anime.setPopularity(root.path("popularity").asInt());

            // Set release date (format: "YYYY-MM-DD")
            JsonNode startDate = root.path("startDate");
            String releaseDate = String.format("%d-%02d-%02d",
                    startDate.path("year").asInt(),
                    startDate.path("month").asInt(),
                    startDate.path("day").asInt());
            anime.setReleaseDate(releaseDate);

        } catch (Exception e) {
            System.err.println("Error parsing API response: " + e.getMessage());
            e.printStackTrace();
        }

        return anime;
    }
}



