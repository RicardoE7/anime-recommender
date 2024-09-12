package com.watchmoreanime.service;

import com.watchmoreanime.domain.Anime;
import com.watchmoreanime.repository.AnimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnimeService {
    @Autowired
    AnimeRepository animeRepository;

    public Anime getAnimeById(Long id){
        return animeRepository.getAnimeById(id);
    }
}
