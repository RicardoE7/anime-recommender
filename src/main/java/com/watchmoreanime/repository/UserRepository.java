package com.watchmoreanime.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.watchmoreanime.domain.Anime;
import com.watchmoreanime.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    
    @Query("SELECT a FROM User u JOIN u.watchList a WHERE u.id = :userId")
    List<Anime> findWatchListByUserId(@Param("userId") Long userId);
    
    @Query("SELECT a, r.score " +
    	       "FROM User u " +
    	       "JOIN u.watchList a " +
    	       "LEFT JOIN Rating r ON r.anime.id = a.id AND r.user.id = :userId " +
    	       "WHERE u.id = :userId")
    	List<Object[]> findWatchListWithRatings(@Param("userId") Long userId);
}

