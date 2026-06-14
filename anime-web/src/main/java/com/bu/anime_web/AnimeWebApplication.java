package com.bu.anime_web;

import com.bu.anime_web.service.AnimeSeriesService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnimeWebApplication {


	public static void main(String[] args) {
		org.springframework.context.ApplicationContext context = SpringApplication.run(AnimeWebApplication.class, args);
		AnimeSeriesService animeSeriesService = context.getBean(AnimeSeriesService.class);
		animeSeriesService.fetchAndSaveEpisodes(1642l);
	}
}