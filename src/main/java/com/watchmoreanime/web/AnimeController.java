package com.watchmoreanime.web;


import java.util.List;

import com.watchmoreanime.service.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.watchmoreanime.domain.Anime;
import com.watchmoreanime.repository.AnimeRepository;

@RestController
@RequestMapping("/api/anime")
public class AnimeController {

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private AnimeService animeService;

    @GetMapping("/anime-details/{animeId}")
    public String getAnimeDetails(@PathVariable("animeId") Long id, Model model) {
        // Retrieve the anime details using the AnimeService
        Anime anime = animeService.getAnimeById(id);

        if (anime != null) {
            // Add anime details to the model
            model.addAttribute("anime", anime);
            return "anime-details"; // The name of your Thymeleaf template without .html
        } else {
            // Handle the case where the anime is not found
            model.addAttribute("error", "Anime not found");
            return "error"; // Redirect to an error page or handle as needed
        }
    }

    @GetMapping
    public List<Anime> getAllAnime() {
        return animeRepository.findAll();
    }

    @GetMapping("/genre/{genre}")
    public List<Anime> getAnimeByGenre(@PathVariable String genre) {
        return animeRepository.findByGenre(genre);
    }
}

