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
        // Record start time
        long startTime = System.currentTimeMillis();

        int rangeStep = 2; // The step size to increment by
        int greater = 0;   // Initial 'greater' value
        int lesser = 2;    // Initial 'lesser' value

        // Loop through the range until 'lesser' exceeds 100
        while (lesser <= 100) {
            // Fetch and save anime synchronously
            animeService.fetchAndSaveAnimeByScoreRange(greater, lesser);
            System.out.println("Fetched and saved anime for score range: " + greater + "-" + lesser);

            // Update the range for the next iteration
            greater += rangeStep;
            lesser += rangeStep;
        }

        // Record end time
        long endTime = System.currentTimeMillis();

        // Calculate elapsed time in seconds
        long elapsedTime = (endTime - startTime) / 1000;

        // Print the elapsed time
        System.out.println("Scheduled Fetch Completed - updateAnimeData() took " + elapsedTime + " seconds to run.");
    }

    // A test method for fetching anime in the Dragon Ball score range (79-81)
    public void dragonBallTest() {
        int greater = 79;
        int lesser = 81;

        // Fetch and save synchronously for this specific range
        animeService.fetchAndSaveAnimeByScoreRange(greater, lesser);
        System.out.println("Fetched Dragon Ball related anime for score range: " + greater + "-" + lesser);
    }
}





