package com.watchmoreanime.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.watchmoreanime.domain.Anime;

public interface AnimeRepository extends JpaRepository<Anime, Long> {
	List<Anime> findByGenre(String genre);
	Anime getAnimeById(Long id);
}
