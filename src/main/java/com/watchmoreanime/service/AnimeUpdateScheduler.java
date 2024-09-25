package com.watchmoreanime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

@Component
public class AnimeUpdateScheduler {

    @Autowired
    private AnimeService animeService;

    private final Semaphore semaphore = new Semaphore(5); // Limit concurrent tasks to 5

    // This method runs daily at 2 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateAnimeData() {
        // Record start time
        long startTime = System.currentTimeMillis();

        int rangeStep = 2; // The step size to increment by
        List<CompletableFuture<Void>> futures = new ArrayList<>(); // A list to store async tasks

        // Using arrays to hold mutable state
        int[] greater = {0}; // Mutable integer array to hold the 'greater' value
        int[] lesser = {2};  // Mutable integer array to hold the 'lesser' value

        // Loop through the range until 'lesser' exceeds 100
        while (lesser[0] <= 100) {
            // Capture the current values for use in the CompletableFuture
            int currentGreater = greater[0];
            int currentLesser = lesser[0];

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // Acquire a permit before proceeding
                    semaphore.acquire();

                    // Fetch and save the anime for the given range
                    animeService.fetchAndSaveAnimeByScoreRange(currentGreater, currentLesser);
                    System.out.println("Fetched and saved anime for score range: " + currentGreater + "-" + currentLesser);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Thread was interrupted during semaphore acquisition");
                } finally {
                    // Release the semaphore permit after task completion
                    semaphore.release();
                }
            });

            futures.add(future); // Add the future to the list

            // Update the mutable state for the next iteration
            greater[0] += rangeStep;
            lesser[0] += rangeStep;
        }

        // Wait for all async tasks to complete
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join(); // Wait for all the tasks to complete

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

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                // Acquire semaphore before running the task
                semaphore.acquire();
                
                animeService.fetchAndSaveAnimeByScoreRange(greater, lesser); // Fetch and save for this specific range
                System.out.println("Fetched Dragon Ball related anime for score range: " + greater + "-" + lesser);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread was interrupted during semaphore acquisition");
            } finally {
                // Release the semaphore after the task is complete
                semaphore.release();
            }
        });

        future.join(); // Wait for the async task to complete
    }
}




