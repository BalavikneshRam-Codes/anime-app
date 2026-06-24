package com.bu.anime_web.scheduler;

import com.bu.anime_web.converter.AnimeConverter;
import com.bu.anime_web.entity.Anime;
import com.bu.anime_web.repository.AnimeRepository;
import com.bu.anime_web.service.AnimeSeriesService;
import com.bu.anime_web.vo.anikotoapi.RecentAnimeResponseVO;
import com.bu.anime_web.entity.Genre;
import com.bu.anime_web.entity.Producer;
import com.bu.anime_web.entity.Studio;
import com.bu.anime_web.repository.GenreRepository;
import com.bu.anime_web.repository.ProducerRepository;
import com.bu.anime_web.repository.StudioRepository;
import com.bu.anime_web.vo.common.AnimeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecentAnimeUpdateScheduler {

    @Autowired
    private AnimeSeriesService animeSeriesService;
    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Kolkata")//1:00 AM
    public void updateRecentAnime() {
        log.info("Starting scheduled task to fetch recent anime...");
        try {
            animeSeriesService.updateAllAnime();
        } catch (Exception e) {
            log.error("Error occurred while fetching recent anime data", e);
        }
    }
}
