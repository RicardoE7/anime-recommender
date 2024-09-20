package com.watchmoreanime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnimeRecommenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnimeRecommenderApplication.class, args);
	}

}
