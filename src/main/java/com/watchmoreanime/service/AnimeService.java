package com.watchmoreanime.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.watchmoreanime.domain.Anime;
import com.watchmoreanime.repository.AnimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        String query = """
        {
            Media(id: %d, type: ANIME) {
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
                popularity
                startDate {
                    year
                    month
                    day
                }
            }
        }
        """.formatted(animeId);

        RestTemplate restTemplate = new RestTemplate();

        // Create the request to AniList API
        String jsonRequest = String.format("{\"query\":\"%s\"}", query);
        String response = restTemplate.postForObject(apiUrl, jsonRequest, String.class);

        // Parse the response into an Anime object
        return parseApiResponse(response);
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
            JsonNode root = mapper.readTree(response).get("data").get("Media");

            // Set anime ID
            anime.setId(root.get("id").asLong());

            // Set anime title (use English if available, otherwise Romaji)
            JsonNode titleNode = root.get("title");
            if (titleNode.has("english") && !titleNode.get("english").isNull()) {
                anime.setTitle(titleNode.get("english").asText());
            } else {
                anime.setTitle(titleNode.get("romaji").asText());
            }

            // Set genres as a list of strings
            List<String> genres = new ArrayList<>();
            JsonNode genresNode = root.get("genres");
            for (JsonNode genreNode : genresNode) {
                genres.add(genreNode.asText());
            }
            anime.setGenres(genres);

            // Set episode count
            anime.setEpisodeCount(root.get("episodes").asInt());

            // Set cover image URL
            anime.setCoverImage(root.get("coverImage").get("large").asText());

            // Set description
            anime.setDescription(root.get("description").asText());

            // Set average score
            anime.setAverageScore(root.get("averageScore").asInt());

            // Set popularity
            anime.setPopularity(root.get("popularity").asInt());

            // Set release date (format: "YYYY-MM-DD")
            JsonNode startDate = root.get("startDate");
            String releaseDate = String.format("%d-%02d-%02d",
                    startDate.get("year").asInt(),
                    startDate.get("month").asInt(),
                    startDate.get("day").asInt());
            anime.setReleaseDate(releaseDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return anime;
    }

}




