package com.watchmoreanime.service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.watchmoreanime.domain.Anime;
import com.watchmoreanime.domain.Rating;
import com.watchmoreanime.domain.User;
import com.watchmoreanime.dto.RegistrationDTO;
import com.watchmoreanime.repository.AnimeRepository;
import com.watchmoreanime.repository.RatingRepository;
import com.watchmoreanime.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AnimeRepository animeRepository;
    
    @Autowired
    private RatingRepository ratingRepository;

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void registerUser(RegistrationDTO registrationDTO) {
        // Check if username already exists
        if (userRepository.findByUsername(registrationDTO.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        // Create a new user
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(registrationDTO.getPassword()); // No encryption

        // Save user to the database
        userRepository.save(user);
    }
    
    public void save(User user) {
    	userRepository.save(user);
    }

    public User authenticate(String username, String password) {
        // Find the user by username
        User user = userRepository.findByUsername(username);
        System.out.println(username);
        System.out.println(password);
        // If the user exists and the password matches, return the user
        if (user != null && user.getPassword().equals(password)) {
        	System.out.println("user not null ________ user.authenticate()");
            return user;
        }
        // Otherwise, return null indicating authentication failure
        return null;
    }

    public boolean addAnimeToWatchlist(Long userId, Long animeId, Integer rating) {
        // Step 1: Retrieve user
    	System.out.println("UserId = " + userId);
    	System.out.println("animeId = " + animeId);
    	System.out.println("rating = " + rating);
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return false; // User does not exist
        }
        User user = userOptional.get();

        // Step 2: Retrieve or create anime
        Anime anime = animeRepository.findById(animeId).orElseGet(() -> {
            Anime newAnime = new Anime();
            newAnime.setId(animeId);
            // You could also fetch additional anime details here if needed
            return animeRepository.save(newAnime);
        });

        // Step 3: Check if the anime is already in the user’s watchlist
        if (user.getWatchList().contains(anime)) {
            return false; // Anime already in watchlist
        }

        // Add anime to user’s watchlist
        user.getWatchList().add(anime);
        userRepository.save(user); // Save user to update the watchlist

        // Step 4: Save the rating
        Rating ratingEntry = new Rating();
        ratingEntry.setUser(user);
        ratingEntry.setAnime(anime);
        ratingEntry.setScore(rating);
        ratingRepository.save(ratingEntry);

        return true; // Successfully added to watchlist and saved the rating
    }

    public List<Anime> getWatchList(Long userId) {
    	System.out.println("The UserId is being passed to the watchlist component " + userId);
    	return userRepository.findWatchListByUserId(userId);
    }
    
    public List<Anime> getWatchListWithRatings(Long userId) {
        List<Object[]> results = userRepository.findWatchListWithRatings(userId);
        return results.stream().map(result -> {
            Anime anime = (Anime) result[0];
            Integer userRating = (Integer) result[1]; // Fetch user's rating
            anime.setUserRating(userRating); // Set transient userRating field
            return anime;
        }).collect(Collectors.toList());
    }

}
