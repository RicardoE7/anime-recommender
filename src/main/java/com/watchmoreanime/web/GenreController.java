package com.watchmoreanime.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.watchmoreanime.domain.Anime;
import com.watchmoreanime.domain.Genre;
import com.watchmoreanime.service.AnimeService;
import com.watchmoreanime.service.GenreService;

@Controller
public class GenreController {
	@Autowired
	private GenreService genreService;
	
	@Autowired
	private AnimeService animeService;
	 @GetMapping("/genres")
	    public ResponseEntity<List<String>> getGenres() {
	    	List<Genre> genreList = genreService.getAllGenres();
	    	List<String> genreStrings = new ArrayList<>();
	    	for(Genre genre : genreList) {
	    		genreStrings.add(genre.getGenre());
	    	}
	    	return ResponseEntity.ok(genreStrings);
	    }
	 
	 @GetMapping("/genre/{genre}")
	 public ResponseEntity<List<Anime>> getAnimeByGenre(@PathVariable String genre) {
	     List<Anime> animeList = animeService.getAnimeByGenre(genre);
	     if (animeList.isEmpty()) {
	         return ResponseEntity.noContent().build(); // No anime found for this genre
	     }
	     return ResponseEntity.ok(animeList);
	 }

}
