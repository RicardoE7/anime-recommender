package com.watchmoreanime.web;


import java.util.List;

import com.watchmoreanime.service.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.watchmoreanime.domain.Anime;
import com.watchmoreanime.repository.AnimeRepository;

@RestController
public class AnimeController {

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private AnimeService animeService;

    @GetMapping("/anime-details/{animeId}")
    public ResponseEntity<Anime> getAnimeDetails(@PathVariable("animeId") Long animeId) {
        // Fetch the anime from AniList API
        Anime anime = animeService.fetchAnimeFromApi(animeId);
        System.out.println(anime);
        if (anime != null) {
            return ResponseEntity.ok(anime);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/anime-details/{animeId}")
    public ResponseEntity<Anime> fetchAndSaveAnime(@PathVariable("animeId") Long animeId) {
        // Fetch the anime from AniList API
        Anime anime = animeService.fetchAnimeFromApi(animeId);

        if (anime != null) {
            // Check if the anime is already in the database
            if (!animeRepository.existsById(animeId)) {
                // Save the anime with genres to the database
                // Handle genre saving or linking if needed
                animeService.saveAnimeWithGenres(anime, anime.getGenres());
            }
            return ResponseEntity.ok(anime);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public List<Anime> getAllAnime() {
        return animeRepository.findAll();
    }

    @GetMapping("/genre/{genre}")
    public List<Anime> getAnimeByGenre(@PathVariable String genre) {
        return animeRepository.findByGenresContaining(genre);
    }
}

