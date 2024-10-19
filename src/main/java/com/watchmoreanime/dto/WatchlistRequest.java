package com.watchmoreanime.dto;

public class WatchlistRequest {
    private Long userId;
    private Long animeId;
    private Integer rating;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getAnimeId() {
		return animeId;
	}
	public void setAnimeId(Long animeId) {
		this.animeId = animeId;
	}
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
    
}
