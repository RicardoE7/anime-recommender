package com.watchmoreanime.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.watchmoreanime.domain.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByUserId(Long userId);
}

