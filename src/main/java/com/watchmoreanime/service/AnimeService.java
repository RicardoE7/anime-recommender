package com.watchmoreanime.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
				        updatedAt
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
			ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, request,
					String.class);
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
	    long animeId = mediaNode.path("id").asLong();
	    
	    Optional<Anime> existingAnimeOpt = animeRepository.findById(animeId);
	    if (existingAnimeOpt.isPresent()) {
	        Anime existingAnime = existingAnimeOpt.get();

	        // Compare relevant fields
	        if (existingAnime.getEpisodeCount() == mediaNode.path("episodes").asInt() &&
	            (existingAnime.getTitle().equals(mediaNode.path("title").path("english").asText()) ||
	            existingAnime.getTitle().equals(mediaNode.path("title").path("romaji").asText()) ||
	            existingAnime.getTitle().equals(mediaNode.path("title").path("native").asText())) &&
	            existingAnime.getPopularity() == mediaNode.path("popularity").asInt() &&
	            existingAnime.getCoverImage().equals(mediaNode.path("coverImage").path("large").asText()) &&
	            existingAnime.getAverageScore() == mediaNode.path("averageScore").asInt()) {
	            
	            // Return the existing anime if no fields have changed
	            return existingAnime;
	        }
	    }

	    // Parsing new or updated anime data
	    try {
	        anime.setId(animeId);

	        JsonNode titleNode = mediaNode.path("title");
	        String title = titleNode.path("english").asText();
	        if (!"null".equals(title)) {
	            anime.setTitle(title);
	        } else if (!"null".equals(titleNode.path("romaji").asText())) {
	            anime.setTitle(titleNode.path("romaji").asText());
	        } else if (!"null".equals(titleNode.path("native").asText())) {
	            anime.setTitle(titleNode.path("native").asText());
	        } else {
	            anime.setTitle("Unknown Title");
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

	        // Clean description using Jsoup
	        String rawDescription = mediaNode.path("description").asText();
	        anime.setDescription(Jsoup.parse(rawDescription).text());

	        anime.setAverageScore(mediaNode.path("averageScore").asInt());
	        anime.setPopularity(mediaNode.path("popularity").asInt());

	        JsonNode startDate = mediaNode.path("startDate");
	        String releaseDate = String.format("%d-%02d-%02d", startDate.path("year").asInt(),
	            startDate.path("month").asInt(), startDate.path("day").asInt());
	        anime.setReleaseDate(releaseDate);

	        // Set the updatedAt field
	        long updatedAtTimestamp = mediaNode.path("updatedAt").asLong();
	        anime.setUpdatedAt(LocalDateTime.ofEpochSecond(updatedAtTimestamp, 0, ZoneOffset.UTC));

	        System.out.println("Parsed Anime: " + anime);

	    } catch (Exception e) {
	        System.err.println("Error parsing single media: " + e.getMessage());
	        e.printStackTrace();
	    }

	    return anime;
	}



	// Fetch Anime by score range from the AniList API and save to the database if
	// not present
	// Fetch Anime by score range from the AniList API with pagination and save to
	// the database if not present
	public List<Anime> fetchAndSaveAnimeByScoreRange(int averageScoreGreater, int averageScoreLesser) {
	    String query = """
	            query ($averageScore_greater: Int, $averageScore_lesser: Int, $page: Int) {
	                Page(page: $page, perPage: 50) {
	                    media(type: ANIME, averageScore_greater: $averageScore_greater, averageScore_lesser: $averageScore_lesser, sort: SCORE_DESC, isAdult: false) {
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
	                        updatedAt
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
	            String jsonRequest = String.format(
	                    "{\"query\":\"%s\",\"variables\":{\"averageScore_greater\":%d,\"averageScore_lesser\":%d,\"page\":%d}}",
	                    formattedQuery, averageScoreGreater, averageScoreLesser, currentPage);
	            HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);

	            try {
	                ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
	                String response = responseEntity.getBody();
	                System.out.println("API Response for Score Range (" + averageScoreGreater + "-" + averageScoreLesser + ") Page " + currentPage + ": " + response);

	                List<Anime> animeList = parseApiResponse(response);
	                allAnime.addAll(animeList);

	                // Save or update each anime
	                for (Anime anime : animeList) {
	                    if (anime != null) {
	                        Optional<Anime> existingAnimeOpt = animeRepository.findById(anime.getId());

	                        if (existingAnimeOpt.isPresent()) {
	                            // Update existing anime
	                            Anime existingAnime = existingAnimeOpt.get();
	                            if(existingAnime.getEpisodeCount() != anime.getEpisodeCount() ||
	                            		existingAnime.getTitle() != anime.getTitle() ||
	                            		existingAnime.getPopularity() != anime.getPopularity() ||
	                            		existingAnime.getCoverImage() != anime.getCoverImage() ||
	                            		existingAnime.getAverageScore() != anime.getAverageScore() ||
	                            		existingAnime.getGenres() != anime.getGenres() ||
	                            		existingAnime.getDescription() != anime.getDescription()
	                            		) {
	                            	updateAnimeFields(existingAnime, anime);
		                            animeRepository.save(existingAnime);
		                            System.out.println("Updated anime with ID: " + anime.getId());
	                            }
	                            
	                        } else {
	                            // Save new anime
	                            saveAnimeWithGenres(anime, anime.getGenres());
	                            System.out.println("Saved new anime with ID: " + anime.getId());
	                        }
	                    }
	                }

	                // Parse page info to check for the next page
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
	            TimeUnit.MILLISECONDS.sleep(750);
	        }

	        System.out.println("Fetched and Saved All Anime by Score Range: " + allAnime.size());
	        return allAnime;

	    } catch (Exception e) {
	        System.err.println("Error fetching and saving anime by score range: " + e.getMessage());
	        e.printStackTrace();
	        return new ArrayList<>();
	    }
	}


	@Transactional
	public void updateAnimeData() {
		System.out.println("Starting Anime Data Update...");

		// Retrieve all anime IDs from the database
		List<Long> allAnimeIds = animeRepository.findAllAnimeIds();
		System.out.println("Anime IDs in database: " + allAnimeIds);

		// Initialize the rest template and headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		int retryCount = 0;
		final int maxRetries = 5;

		// Fetch all anime data from AniList API, for instance, in batches of 50
		List<Anime> apiAnimeList = fetchAllAnimeFromApi(); // Implement this method to retrieve all anime from API

		for (Anime apiAnime : apiAnimeList) {
			try {
				Long animeId = apiAnime.getId();

				// Check if the anime is already in the database
				if (!allAnimeIds.contains(animeId)) {
					// Anime is not in the database, add it
					saveAnimeWithGenres(apiAnime, apiAnime.getGenres());
					System.out.println("New Anime Added: " + apiAnime);
				} else {
					// Anime is in the database, check if an update is needed
					Anime existingAnime = getAnimeById(animeId);
					if (existingAnime != null && (existingAnime.getUpdatedAt() == null
							|| apiAnime.getUpdatedAt().isAfter(existingAnime.getUpdatedAt()))) {
						// Update the existing anime entry with new data
						saveAnimeWithGenres(apiAnime, apiAnime.getGenres());
						System.out.println("Updated Anime: " + apiAnime);
					} else {
						System.out.println("No update needed for Anime ID: " + animeId);
					}
				}

				// Respect the rate limit (90 calls per minute -> approximately 0.67 seconds
				// between calls)
				TimeUnit.SECONDS.sleep(60 / 90);

			} catch (Exception e) {
				if (e.getMessage().contains("429")) {
					// Handle rate limit error
					System.err.println(
							"Rate limit exceeded. Retrying in " + (Math.pow(2, retryCount) * 10) + " seconds...");
					try {
						TimeUnit.SECONDS.sleep((long) Math.pow(2, retryCount) * 10);
					} catch (InterruptedException ie) {
						System.err.println("Interrupted while waiting: " + ie.getMessage());
					}
					retryCount++;
					if (retryCount > maxRetries) {
						System.err.println("Max retries reached. Skipping update for Anime ID: " + apiAnime.getId());
						retryCount = 0; // Reset retry count
					}
				} else {
					// Handle other errors
					System.err.println("Error updating Anime ID " + apiAnime.getId() + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		System.out.println("Anime Data Update Completed.");
	}

	private List<Anime> fetchAllAnimeFromApi() {
		List<Anime> allAnime = new ArrayList<>();
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		int currentPage = 1;
		boolean hasNextPage = true;

		// Define the GraphQL query with a variable placeholder for the page number
		String query = """
				query ($page: Int) {
				    Page(page: $page, perPage: 50) {
				        media(type: ANIME) {
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
				            updatedAt
				        }
				        pageInfo {
				            hasNextPage
				            currentPage
				        }
				    }
				}
				""";

		while (hasNextPage) {
			try {
				// Create a map for the GraphQL variables
				Map<String, Object> variables = new HashMap<>();
				variables.put("page", currentPage);

				// Create the request body including the query and variables
				ObjectMapper mapper = new ObjectMapper();
				String jsonRequest = String.format("{\"query\":\"%s\", \"variables\": %s}",
						query.replace("\n", "\\n").replace("\"", "\\\""), mapper.writeValueAsString(variables));

				HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

				// Make the POST request to the API
				ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

				if (response.getStatusCode() == HttpStatus.OK) {
					// Parse the response
					JSONObject json = new JSONObject(response.getBody());
					JSONObject pageData = json.getJSONObject("data").getJSONObject("Page");
					JSONArray mediaArray = pageData.getJSONArray("media");
					hasNextPage = pageData.getJSONObject("pageInfo").getBoolean("hasNextPage");
					currentPage = pageData.getJSONObject("pageInfo").getInt("currentPage") + 1;

					for (int i = 0; i < mediaArray.length(); i++) {
						JSONObject animeJson = mediaArray.getJSONObject(i);
						Anime anime = parseAnimeFromJson(animeJson);
						if (anime != null) {
							allAnime.add(anime);
						}
					}
				} else {
					System.err.println("Failed to fetch anime data. Response status: " + response.getStatusCode());
					hasNextPage = false;
				}
			} catch (Exception e) {
				System.err.println("Error fetching anime data: " + e.getMessage());
				e.printStackTrace();
				hasNextPage = false;
			}

			// Delay to respect the rate limit (90 requests per minute)
			try {
				TimeUnit.SECONDS.sleep(60 / 90);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		return allAnime;
	}

	private Anime parseAnimeFromJson(JSONObject animeJson) {
		Anime anime = new Anime();

		try {
			anime.setId(animeJson.getLong("id"));

			JSONObject title = animeJson.getJSONObject("title");
			anime.setTitle(title.has("english") ? title.getString("english") : title.getString("romaji"));

			List<String> genres = new ArrayList<>();
			JSONArray genresArray = animeJson.getJSONArray("genres");
			for (int i = 0; i < genresArray.length(); i++) {
				genres.add(genresArray.getString(i));
			}
			anime.setGenres(genres);

			anime.setEpisodeCount(animeJson.optInt("episodes", 0));
			anime.setCoverImage(animeJson.getJSONObject("coverImage").getString("large"));

			String rawDescription = animeJson.optString("description", "");
			anime.setDescription(Jsoup.parse(rawDescription).text());

			anime.setAverageScore(animeJson.optInt("averageScore", 0));
			anime.setPopularity(animeJson.optInt("popularity", 0));

			JSONObject startDate = animeJson.getJSONObject("startDate");
			String releaseDate = String.format("%d-%02d-%02d", startDate.getInt("year"), startDate.getInt("month"),
					startDate.getInt("day"));
			anime.setReleaseDate(releaseDate);

			long updatedAtTimestamp = animeJson.getLong("updatedAt");
			anime.setUpdatedAt(LocalDateTime.ofEpochSecond(updatedAtTimestamp, 0, ZoneOffset.UTC));
		} catch (Exception e) {
			System.err.println("Error parsing Anime JSON: " + e.getMessage());
			e.printStackTrace();
		}

		return anime;
	}
	
	public List<Anime> findByAverageScoreBetween(int averageScoreGreater, int averageScoreLesser){
		System.out.println("Lower Bound: " + averageScoreGreater + ", Upper Bound: " + averageScoreLesser);
		return animeRepository.findByAverageScoreBetween(averageScoreGreater, averageScoreLesser);
	}
	
	private void updateAnimeFields(Anime existingAnime, Anime newAnime) {
	    existingAnime.setTitle(newAnime.getTitle());
	    existingAnime.setGenres(newAnime.getGenres());
	    existingAnime.setEpisodeCount(newAnime.getEpisodeCount());
	    existingAnime.setCoverImage(newAnime.getCoverImage());
	    existingAnime.setDescription(newAnime.getDescription());
	    existingAnime.setAverageScore(newAnime.getAverageScore());
	    existingAnime.setPopularity(newAnime.getPopularity());
	    existingAnime.setReleaseDate(newAnime.getReleaseDate());
	    existingAnime.setUpdatedAt(newAnime.getUpdatedAt());
	}


}
