package com.watchmoreanime.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.watchmoreanime.domain.Anime;
import com.watchmoreanime.repository.AnimeRepository;

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

        String formattedQuery = query.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"");
        String jsonRequest = String.format("{\"query\":\"%s\",\"variables\":{\"id\":%d}}", formattedQuery, animeId);
        HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
            String response = responseEntity.getBody();
            System.out.println("API Response for Anime ID " + animeId + ": " + response);
            List<Anime> animeList = parseApiResponse(response);
            return animeList.isEmpty() ? null : animeList.get(0);

        } catch (Exception e) {
            System.err.println("Error during API call for Anime ID " + animeId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Saving anime to the database with genres directly as strings
    @Transactional
    public void saveAnimeWithGenres(Anime anime, List<String> genreNames) {
        anime.setGenres(genreNames);
        animeRepository.save(anime);
        System.out.println("Saved Anime to database: " + anime);
    }

    // Function to parse API response to a list of Anime objects
    public List<Anime> parseApiResponse(String response) {
        ObjectMapper mapper = new ObjectMapper();
        List<Anime> animeList = new ArrayList<>();

        try {
            JsonNode root = mapper.readTree(response);

            if (root.path("data").has("Media")) {
                // Single media object
                JsonNode mediaNode = root.path("data").path("Media");
                Anime anime = parseSingleMedia(mediaNode);
                if (anime != null) {
                    animeList.add(anime);
                }
            } else if (root.path("data").path("Page").path("media").isArray()) {
                // List of media objects
                JsonNode mediaArray = root.path("data").path("Page").path("media");

                for (JsonNode node : mediaArray) {
                    Anime anime = parseSingleMedia(node);
                    if (anime != null) {
                        animeList.add(anime);
                    }
                }
            } else {
                System.err.println("Unexpected response format: " + response);
            }

        } catch (Exception e) {
            System.err.println("Error parsing API response: " + e.getMessage());
            e.printStackTrace();
        }

        return animeList;
    }

    private Anime parseSingleMedia(JsonNode mediaNode) {
        Anime anime = new Anime();

        try {
            anime.setId(mediaNode.path("id").asLong());

            JsonNode titleNode = mediaNode.path("title");
            if (titleNode.has("english") && !titleNode.path("english").isMissingNode()) {
                anime.setTitle(titleNode.path("english").asText());
            } else {
                anime.setTitle(titleNode.path("romaji").asText());
            }

            List<String> genres = new ArrayList<>();
            JsonNode genresNode = mediaNode.path("genres");
            if (genresNode.isArray()) {
                for (JsonNode genreNode : genresNode) {
                    genres.add(genreNode.asText());
                }
            }
            anime.setGenres(genres);
            anime.setEpisodeCount(mediaNode.path("episodes").asInt());
            anime.setCoverImage(mediaNode.path("coverImage").path("large").asText());

            // Use Jsoup to clean the description
            String rawDescription = mediaNode.path("description").asText();
            String cleanDescription = Jsoup.parse(rawDescription).text();
            anime.setDescription(cleanDescription);

            anime.setAverageScore(mediaNode.path("averageScore").asInt());
            anime.setPopularity(mediaNode.path("popularity").asInt());

            JsonNode startDate = mediaNode.path("startDate");
            String releaseDate = String.format("%d-%02d-%02d",
                    startDate.path("year").asInt(),
                    startDate.path("month").asInt(),
                    startDate.path("day").asInt());
            anime.setReleaseDate(releaseDate);

            System.out.println("Parsed Anime: " + anime);

        } catch (Exception e) {
            System.err.println("Error parsing single media: " + e.getMessage());
            e.printStackTrace();
        }

        return anime;
    }

    // Fetch Anime by score range from the AniList API and save to the database if not present
 // Fetch Anime by score range from the AniList API with pagination and save to the database if not present
    public List<Anime> fetchAndSaveAnimeByScoreRange(int averageScoreGreater, int averageScoreLesser) {
        String query = """
        query ($averageScore_greater: Int, $averageScore_lesser: Int, $page: Int) {
            Page(page: $page, perPage: 50) {
                media(type: ANIME, averageScore_greater: $averageScore_greater, averageScore_lesser: $averageScore_lesser, sort: SCORE_DESC) {
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
                pageInfo {
                    currentPage
                    lastPage
                    hasNextPage
                }
            }
        }
        """;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        int currentPage = 1;
        List<Anime> allAnime = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            boolean hasNextPage = true;
            int retryCount = 0;
            final int maxRetries = 5;

            while (hasNextPage) {
                String formattedQuery = query.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"");
                String jsonRequest = String.format("{\"query\":\"%s\",\"variables\":{\"averageScore_greater\":%d,\"averageScore_lesser\":%d,\"page\":%d}}",
                        formattedQuery, averageScoreGreater, averageScoreLesser, currentPage);
                HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);

                try {
                    ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
                    String response = responseEntity.getBody();
                    System.out.println("API Response for Score Range (" + averageScoreGreater + "-" + averageScoreLesser + ") Page " + currentPage + ": " + response);

                    List<Anime> animeList = parseApiResponse(response);
                    allAnime.addAll(animeList);

                    // Save each anime if it's not already in the database
                    for (Anime anime : animeList) {
                        if (anime != null && !animeRepository.existsById(anime.getId())) {
                            saveAnimeWithGenres(anime, anime.getGenres());
                        }
                    }

                    // Parse page info to check for next page
                    JsonNode root = mapper.readTree(response);
                    JsonNode pageInfo = root.path("data").path("Page").path("pageInfo");

                    hasNextPage = pageInfo.path("hasNextPage").asBoolean();
                    currentPage = pageInfo.path("currentPage").asInt() + 1;

                    // Reset retry count on successful response
                    retryCount = 0;

                } catch (Exception e) {
                    if (e.getMessage().contains("429")) {
                        // Handle rate limit error
                        System.err.println("Rate limit exceeded. Retrying in " + (Math.pow(2, retryCount) * 10) + " seconds...");
                        TimeUnit.SECONDS.sleep((long) Math.pow(2, retryCount) * 10);
                        retryCount++;
                        if (retryCount > maxRetries) {
                            throw new RuntimeException("Max retries reached. Unable to fetch data.", e);
                        }
                    } else {
                        // Handle other errors
                        System.err.println("Error fetching data: " + e.getMessage());
                        throw e;
                    }
                }

                // Delay to respect the rate limit (approximately 0.67 seconds between requests)
                TimeUnit.SECONDS.sleep(60 / 90);
            }

            System.out.println("Fetched and Saved All Anime by Score Range: " + allAnime);
            return allAnime;

        } catch (Exception e) {
            System.err.println("Error fetching and saving anime by score range: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}






