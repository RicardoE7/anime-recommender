package com.watchmoreanime.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.watchmoreanime.domain.Anime;
import com.watchmoreanime.repository.AnimeRepository;
import com.watchmoreanime.service.AnimeService;
import com.watchmoreanime.service.AnimeUpdateScheduler;
import com.watchmoreanime.service.UserService;

@Controller
public class AnimeController {

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private AnimeUpdateScheduler animeScheduler;

    @Autowired
    private AnimeService animeService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/anime-details/{animeId}")
    public String getAnimeDetails(@PathVariable("animeId") Long animeId, Model model) {
        // Fetch the anime from AniList API or the database
        Anime anime = animeService.getAnimeById(animeId);
        if (anime == null) {
            // If anime is not found in the database, fetch from API
            anime = animeService.fetchAnimeFromApi(animeId);
            if (anime != null) {
                // Save to the database if needed
                animeService.saveAnimeWithGenres(anime, anime.getGenres());
            }
        }

        if (anime != null) {
            model.addAttribute("anime", anime);
            return "anime-details"; // Return the name of your HTML template
        } else {
            return "error"; // Return an error page or handle as needed
        }
    }

    @GetMapping
    public String getAllAnime(Model model) {
        List<Anime> animes = animeRepository.findAll();
        model.addAttribute("animes", animes);
        return "anime-list"; // Return the name of your HTML template
    }


    @GetMapping("/anime-range")
    public String fetchAnimeByScoreRange(
            @RequestParam("averageScoreGreater") int averageScoreGreater,
            @RequestParam("averageScoreLesser") int averageScoreLesser,
            Model model) {

        // Fetch anime from an external source and save to the database if needed
        List<Anime> fetchedAnime = animeService.findByAverageScoreBetween(averageScoreGreater, averageScoreLesser);
        System.out.println(
                " From Controller -> Lower Bound: " + averageScoreGreater + ", Upper Bound: " + averageScoreLesser);
        // Add the fetched anime to the model
        model.addAttribute("animes", fetchedAnime);

        return "anime-by-range";
    }

    @PostMapping("/test-scheduler")
    public ResponseEntity<String> fetchAnimeData() {
        animeScheduler.updateAnimeData();
        // animeScheduler.dragonBallTest();
        return ResponseEntity.ok("Anime data fetch initiated.");
    }

    @GetMapping("/recommended-anime/{userId}")
    public ResponseEntity<List<Anime>> getTop10RecommendedAnime(@PathVariable Long userId) {
        List<Anime> topAnime = animeService.getTop10AnimeExcludingWatchlist(userId);
        return ResponseEntity.ok(topAnime);
    }

    @GetMapping("/top-anime/{userId}") // Adjusted endpoint for API
    public ResponseEntity<List<Anime>> getTop10HighestRatedAnime(@PathVariable Long userId) {
        List<Anime> topAnime = animeService.getTop10AnimeByAverageScoreExcludingWatchlist(userId);
        return ResponseEntity.ok(topAnime); // Return the top anime as JSON
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Anime>> searchAnime(@RequestParam("query") String query) {
        List<Anime> results = animeService.searchAnimeByTitle(query);
        return ResponseEntity.ok(results); // Return JSON response
    }
    
    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<Anime>> getRecommendedAnime(@PathVariable Long userId) {
        List<Anime> recommendedAnime = animeService.getRecommendationsBasedOnWatchlist(userService.getWatchList(userId));
        return ResponseEntity.ok(recommendedAnime); // Return the top anime as JSON
    }
   
}
