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

@Controller
public class AnimeController {

    @Autowired
    private AnimeRepository animeRepository;
    
    @Autowired
    private AnimeUpdateScheduler animeScheduler;

    @Autowired
    private AnimeService animeService;

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

    @GetMapping("/genre/{genre}")
    public String getAnimeByGenre(@PathVariable String genre, Model model) {
        List<Anime> animes = animeRepository.findByGenresContaining(genre);
        model.addAttribute("animes", animes);
        return "anime-genre"; // Return the name of your HTML template
    }
    
    @GetMapping("/anime-range")
    public String fetchAnimeByScoreRange(
        @RequestParam("averageScoreGreater") int averageScoreGreater,
        @RequestParam("averageScoreLesser") int averageScoreLesser,
        Model model) {
        
        // Fetch anime from an external source and save to the database if needed
        List<Anime> fetchedAnime = animeService.fetchAndSaveAnimeByScoreRange(averageScoreGreater, averageScoreLesser);
        
     // Add the fetched anime to the model
        model.addAttribute("animes", fetchedAnime);
        
        return "anime-by-range";
    }
    
    @PostMapping("/test-scheduler")
    public ResponseEntity<String> fetchAnimeData() {
        animeScheduler.updateAnimeData();
        return ResponseEntity.ok("Anime data fetch initiated.");
    }
}


