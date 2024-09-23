package com.watchmoreanime.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AnimeUpdateScheduler {

    @Autowired
    private AnimeService animeService;

    // This method runs daily at 2 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateAnimeData() {
        int greater = 0;
        int lesser = 10;
        
        while(lesser <= 100) {
        	animeService.fetchAndSaveAnimeByScoreRange(greater, lesser);
        	greater = greater + 10;
        	lesser = lesser + 10;
        }
        		
    }
}

