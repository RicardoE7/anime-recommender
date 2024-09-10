package com.watchmoreanime.web;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.watchmoreanime.domain.Anime;
import com.watchmoreanime.repository.AnimeRepository;

@RestController
@RequestMapping("/api/anime")
public class AnimeController {

    @Autowired
    private AnimeRepository animeRepository;

    @GetMapping
    public List<Anime> getAllAnime() {
        return animeRepository.findAll();
    }

    @GetMapping("/genre/{genre}")
    public List<Anime> getAnimeByGenre(@PathVariable String genre) {
        return animeRepository.findByGenre(genre);
    }
}

