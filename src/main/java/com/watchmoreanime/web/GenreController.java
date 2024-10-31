package com.watchmoreanime.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.watchmoreanime.domain.Genre;
import com.watchmoreanime.service.GenreService;

@Controller
public class GenreController {
	@Autowired
	private GenreService genreService;
	 @GetMapping("/genres")
	    public ResponseEntity<List<String>> getGenres() {
	    	List<Genre> genreList = genreService.getAllGenres();
	    	List<String> genreStrings = new ArrayList<>();
	    	for(Genre genre : genreList) {
	    		System.out.println(genre);
	    		genreStrings.add(genre.getGenre());
	    	}
	    	return ResponseEntity.ok(genreStrings);
	    }
}
