package com.watchmoreanime.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "anime")
public class Anime {

	@Id
	@Column(unique = true, nullable = false)
	private Long id;

	@Column(nullable = false)
	private String title;

	@ElementCollection
	@CollectionTable(
			name = "anime_genres",
			joinColumns = @JoinColumn(name = "anime_id")
	)
	@Column(name = "genre")
	private List<String> genres;

	@Column(nullable = false)
	private int episodeCount;

	@Column(nullable = false)
	private String coverImage;

	@Column(nullable = false, columnDefinition="LONGTEXT")
	private String description;

	@Column(nullable = false)
	private int averageScore;

	@Column(nullable = false)
	private int popularity;

	@Column(nullable = false)
	private String releaseDate;
	
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres2) {
		this.genres = genres2;
	}

	public int getEpisodeCount() {
		return episodeCount;
	}

	public void setEpisodeCount(int episodeCount) {
		this.episodeCount = episodeCount;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(int averageScore) {
		this.averageScore = averageScore;
	}

	public int getPopularity() {
		return popularity;
	}

	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	
	
}
