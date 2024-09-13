package com.watchmoreanime.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.watchmoreanime.domain.Anime;
import org.springframework.data.jpa.repository.Query;

public interface AnimeRepository extends JpaRepository<Anime, Long> {
	List<Anime> findByGenresContaining(String genreName);

	Anime getAnimeById(Long id);
}
