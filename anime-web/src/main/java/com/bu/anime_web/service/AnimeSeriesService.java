package com.bu.anime_web.service;

import com.bu.anime_web.converter.AnimeConverter;
import com.bu.anime_web.entity.EmbedUrl;
import com.bu.anime_web.entity.Anime;
import com.bu.anime_web.entity.Episode;
import com.bu.anime_web.repository.AnimeRepository;
import com.bu.anime_web.repository.EpisodeRepository;
import com.bu.anime_web.vo.anikotoapi.EpisodeVO;
import com.bu.anime_web.vo.anikotoapi.RecentAnimeResponseVO;
import com.bu.anime_web.vo.anikotoapi.SeriesResponseVO;
import com.bu.anime_web.vo.common.AnimeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class AnimeSeriesService {
    @Autowired
    private EpisodeRepository episodeRepository;
    @Autowired
    private AnimeRepository animeRepository;
    @Autowired
    private RestClient restClient;
    @Autowired
    private AnimeConverter animeConverter;

    public void fetchAndSaveEpisodes(Long animeId) {
        String url = "https://anikotoapi.site/series/" + animeId;
        log.info("Fetching series data for animeId: {}", animeId);
        try {
            SeriesResponseVO response = null;
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    response = restClient.get().uri(url).retrieve().body(SeriesResponseVO.class);
                    break;
                } catch (org.springframework.web.client.HttpClientErrorException e) {
                    if (e.getStatusCode().value() == 429) {
                        log.warn("Rate limited on animeId {}. Retrying in 5 seconds...", animeId);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    } else {
                        throw e;
                    }
                }
            }
            if (response != null && response.isOk() && response.getData() != null && response.getData().getEpisodes() != null) {
                List<EpisodeVO> episodeVOs = response.getData().getEpisodes();
                
                // Deduplicate incoming episodes by their episodeEmbedId or ID to avoid issues
                Map<String, EpisodeVO> distinctEpisodes = episodeVOs.stream()
                        .filter(e -> e.getEpisodeEmbedId() != null)
                        .collect(Collectors.toMap(
                                EpisodeVO::getEpisodeEmbedId,
                                e -> e,
                                (existing, replacement) -> existing
                        ));

                Anime animeReference = animeRepository.findByAnimeId(animeId).orElse(null);
                if (animeReference == null) {
                    log.warn("Cannot save episodes: Anime not found with id {}", animeId);
                    return;
                }

                List<Episode> episodesToSave = new ArrayList<>();
                for (EpisodeVO dto : distinctEpisodes.values()) {
                    Episode episode = episodeRepository.findByAnimeAnimeIdAndEpisodeEmbedId(animeId, dto.getEpisodeEmbedId())
                            .orElse(null);
                    if (episode == null) {
                        episode = new Episode();
                    } else {
                        // Skip if already up-to-date
                        if (episode.getUpdateAt() != null && episode.getUpdateAt().equals(dto.getUpdatedAt())) {
                            continue;
                        }
                    }
                    episode.setAnime(animeReference);
                    episode.setTitle(dto.getTitle());
                    episode.setJpTitle(dto.getJpTitle());
                    
                    if (dto.getNumber() != null) {
                        episode.setOrder(dto.getNumber().longValue());
                    }
                    
                    episode.setEpisodeEmbedId(dto.getEpisodeEmbedId());
                    
                    if (dto.getEmbedUrl() != null) {
                        EmbedUrl embed = episode.getEmbedUrl() != null ? episode.getEmbedUrl() : new EmbedUrl();
                        embed.setSub(dto.getEmbedUrl().getSub());
                        embed.setDub(dto.getEmbedUrl().getDub());
                        episode.setEmbedUrl(embed);
                    }
                    
                    episode.setUpdateAt(dto.getUpdatedAt());
                    
                    episodesToSave.add(episode);
                }
                
                episodeRepository.saveAll(episodesToSave);
                log.info("Successfully fetched and saved {} episodes for animeId {}", episodesToSave.size(), animeId);
            } else {
                log.warn("Invalid response or no episodes found for animeId: {}", animeId);
            }
        } catch (Exception e) {
            log.error("Failed to fetch/save episodes for animeId: " + animeId, e);
        }
    }
    public void updateAllAnime(){
        IntStream.rangeClosed(1, 30).forEach(i -> {
            String url = String.format("https://anikotoapi.site/recent-anime?page=%d&per_page=100", i);
            RecentAnimeResponseVO response = null;
            int maxRetries = 3;
            for (int attempt = 0; attempt < maxRetries; attempt++) {
                try {
                    response = restClient.get().uri(url).retrieve().body(RecentAnimeResponseVO.class);
                    break;
                } catch (org.springframework.web.client.HttpClientErrorException e) {
                    if (e.getStatusCode().value() == 429) {
                        log.warn("Rate limited on page {}. Retrying in 5 seconds...", i);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    } else {
                        throw e;
                    }
                }
            }
            if (response != null && response.isOk() && response.getData() != null) {
                List<Anime> animesToSave = new ArrayList<>();
                List<Long> animeIdsToFetchEpisodes = new ArrayList<>();

                for (AnimeVO dto : response.getData()) {
                    Optional<Anime> existingOpt = animeRepository.findByAnimeId(dto.getId());
                    if (existingOpt.isEmpty()) {
                        Anime newAnime = new Anime();
                        animeConverter.mapToEntity(dto, newAnime);
                        animesToSave.add(newAnime);
                        animeIdsToFetchEpisodes.add(dto.getId()); // Needs episodes
                    } else {
                        Anime existingAnime = existingOpt.get();
                        if (!Objects.equals(existingAnime.getUpdatedAt(), dto.getUpdatedAt())) {
                            animeConverter.mapToEntity(dto, existingAnime);
                            animesToSave.add(existingAnime);
                            animeIdsToFetchEpisodes.add(dto.getId()); // Needs episodes
                        }
                    }
                }

                // MUST save the anime first before fetching episodes
                // Otherwise AnimeSeriesService won't find the parent Anime in the DB!
                animeRepository.saveAll(animesToSave);
                log.info("Successfully fetched and saved {} distinct anime records.", animesToSave.size());

                // Now fetch episodes only for the newly added or updated animes
                for (Long animeId : animeIdsToFetchEpisodes) {
                    fetchAndSaveEpisodes(animeId);
                    // Sleep for 1.5 seconds between episode fetches to prevent 429 Too Many Requests
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } else {
                log.warn("Received empty or invalid response from the API.");
            }

            // Sleep for 2 seconds between fetching pages to prevent 429 Too Many Requests
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
