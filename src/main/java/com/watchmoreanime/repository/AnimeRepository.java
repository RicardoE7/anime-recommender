package com.watchmoreanime.repository;

import java.util.List;

import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.watchmoreanime.domain.Anime;

public interface AnimeRepository extends JpaRepository<Anime, Long> {
	List<Anime> findByGenresContaining(String genreName);

	Anime getAnimeById(Long id);
	
	@Query("SELECT a FROM Anime a WHERE a.averageScore >= :averageScoreGreater AND a.averageScore <= :averageScoreLesser")
	List<Anime> findByAverageScoreBetween(@Param("averageScoreGreater") int averageScoreGreater, @Param("averageScoreLesser") int averageScoreLesser);
	
	// Custom query to retrieve only the IDs of all anime
    @Query("SELECT a.id FROM Anime a")
    List<Long> findAllAnimeIds();

	@Query(value = "SELECT a FROM Anime a ORDER BY a.averageScore DESC")
    List<Anime> findByAverageScore();

	@Query(value = "SELECT a FROM Anime a ORDER BY a.popularity DESC")
    List<Anime> findByPopularity();

	@Query(value = "SELECT a FROM Anime a ORDER BY a.averageScore DESC")
    List<Anime> findTop10ByAverageScoreLimit(Pageable pageable);

	@Query(value = "SELECT a FROM Anime a ORDER BY a.popularity DESC")
    List<Anime> findTop10ByPopularityLimit(Pageable pageable);
	
	// Custom query method to search by title, case-insensitive and partial match
    List<Anime> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT a FROM Anime a JOIN a.genres g WHERE g = :genre")
    List<Anime> findByGenre(@Param("genre") String genre);
    
    @Query("SELECT a FROM Anime a WHERE a.id NOT IN :watchlistIds " +
    	       "AND EXISTS (SELECT g FROM a.genres g WHERE g IN :topGenres) " +
    	       "ORDER BY CASE WHEN a.id IN :highlyRatedIds THEN 1 ELSE 2 END, " +
    	       "a.averageScore DESC, a.popularity DESC")
    	List<Anime> findRecommendedAnimeWithWeights(
    	    @Param("watchlistIds") List<Long> watchlistIds,
    	    @Param("topGenres") List<String> topGenres,
    	    @Param("highlyRatedIds") List<Long> highlyRatedIds
    	);

    @Query("SELECT a FROM Anime a WHERE a.id NOT IN :watchlistIds AND (EXISTS (SELECT g FROM a.genres g WHERE g IN :topGenres) OR a.id IN :highlyRatedIds)")
    List<Anime> findPotentialRecommendations(@Param("watchlistIds") List<Long> watchlistIds, 
                                             
                                             @Param("highlyRatedIds") List<Long> highlyRatedIds,@Param("topGenres") List<String> topGenres);



    
}
