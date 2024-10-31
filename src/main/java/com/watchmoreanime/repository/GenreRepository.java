package com.watchmoreanime.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.watchmoreanime.domain.Genre;

public interface GenreRepository extends JpaRepository<Genre, String> {
    // Custom queries can be added here if needed
}
