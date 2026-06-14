package com.bu.anime_web.service;

import com.bu.anime_web.entity.EmbedUrl;
import com.bu.anime_web.entity.Anime;
import com.bu.anime_web.entity.Episode;
import com.bu.anime_web.repository.AnimeRepository;
import com.bu.anime_web.repository.EpisodeRepository;
import com.bu.anime_web.vo.anikotoapi.EpisodeVO;
import com.bu.anime_web.vo.anikotoapi.SeriesResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnimeSeriesService {
    @Autowired
    private EpisodeRepository episodeRepository;
    @Autowired
    private AnimeRepository animeRepository;
    private  RestTemplate restTemplate = new RestTemplate();

    public void fetchAndSaveEpisodes(Long animeId) {
        String url = "https://anikotoapi.site/series/" + animeId;
        log.info("Fetching series data for animeId: {}", animeId);
        try {
            SeriesResponseVO response = null;
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    response = restTemplate.getForObject(url, SeriesResponseVO.class);
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
}
