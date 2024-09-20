package com.watchmoreanime.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.watchmoreanime.domain.Anime;

public interface AnimeRepository extends JpaRepository<Anime, Long> {
	List<Anime> findByGenresContaining(String genreName);

	Anime getAnimeById(Long id);
	
	@Query("SELECT a FROM Anime a WHERE a.averageScore > :averageScoreGreater AND a.averageScore <= :averageScoreLesser")
	List<Anime> findByAverageScoreBetween(@Param("averageScoreGreater") int averageScoreGreater, @Param("averageScoreLesser") int averageScoreLesser);
	
	// Custom query to retrieve only the IDs of all anime
    @Query("SELECT a.id FROM Anime a")
    List<Long> findAllAnimeIds();
}
