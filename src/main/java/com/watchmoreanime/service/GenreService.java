package com.watchmoreanime.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.watchmoreanime.domain.Genre;
import com.watchmoreanime.repository.GenreRepository;

@Service
public class GenreService {

    @Autowired
    private AnimeService animeRepository;
    private final GenreRepository genreRepository;
    private ConcurrentHashMap<String, Genre> genreCache = new ConcurrentHashMap<>();

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

//    @PostConstruct
//    public void init() {
//        // Load genres from the database into cache on startup
//        List<Anime> animeList = animeRepository.getAllAnime();
//        HashSet<Genre> genreSet = new HashSet<>();
//
//        for (Anime anime : animeList) {
//            // Initialize the genres to avoid LazyInitializationException
//            Hibernate.initialize(anime.getGenres());
//            for (String genreName : anime.getGenres()) {
//                Genre genre = new Genre(genreName);
//                genre.setGenre(genreName);
//                genreSet.add(genre);
//            }
//        }
//
//        for (Genre genre : genreSet) {
//            genreRepository.save(genre);
//        }
//    }


    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre getGenre(String name) {
        return genreCache.get(name);
    }

    public void addGenre(Genre genre) {
        genreCache.put(genre.getGenre(), genre);
        genreRepository.save(genre); // Persist in database
    }

    public void removeGenre(String name) {
        genreCache.remove(name);
        genreRepository.deleteById(name); // Remove from database
    }

    public void refreshCache() {
        genreCache.clear(); // Clear the cache
        List<Genre> genres = genreRepository.findAll(); // Reload from DB
        for (Genre genre : genres) {
            genreCache.put(genre.getGenre(), genre);
        }
    }
}

