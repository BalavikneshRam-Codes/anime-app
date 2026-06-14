package com.bu.anime_web.scheduler;

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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecentAnimeUpdateScheduler {

    private final AnimeRepository animeRepository;
    private final AnimeSeriesService animeSeriesService;
    private final GenreRepository genreRepository;
    private final ProducerRepository producerRepository;
    private final StudioRepository studioRepository;
    private final RestClient restClient = RestClient.create();

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Kolkata")//1:00 AM
    public void updateRecentAnime() {
        log.info("Starting scheduled task to fetch recent anime...");
        try {
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
                            mapToEntity(dto, newAnime);
                            animesToSave.add(newAnime);
                            animeIdsToFetchEpisodes.add(dto.getId()); // Needs episodes
                        } else {
                            Anime existingAnime = existingOpt.get();
                            if (!Objects.equals(existingAnime.getUpdatedAt(), dto.getUpdatedAt())) {
                                mapToEntity(dto, existingAnime);
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
                        animeSeriesService.fetchAndSaveEpisodes(animeId);
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
        } catch (Exception e) {
            log.error("Error occurred while fetching recent anime data", e);
        }
    }

    private void mapToEntity(AnimeVO dto, Anime anime) {
        anime.setAnimeId(dto.getId());
        anime.setTitle(dto.getTitle());
        anime.setAlternative(dto.getAlternative());
        anime.setTitles(dto.getTitles());
        anime.setNativeTitle(dto.getNativeTitle());
        anime.setSlug(dto.getSlug());
        anime.setRating(dto.getRating());
        anime.setPoster(dto.getPoster());
        anime.setIsSub(dto.getIsSub());
        anime.setDescription(dto.getDescription());
        anime.setAired(dto.getAired());
        anime.setSeason(dto.getSeason());
        anime.setYear(dto.getYear());
        anime.setDuration(dto.getDuration());
        anime.setStatus(dto.getStatus());
        anime.setScore(dto.getScore());
        anime.setMalId(dto.getMalId());
        anime.setEpisodesNum(dto.getEpisodes());
        anime.setAniId(dto.getAniId());
        anime.setSource(dto.getSource());
        anime.setBackgroundImage(dto.getBackgroundImage());
        anime.setUpdatedAt(dto.getUpdatedAt());
        anime.setNextAirScheduleTime(dto.getNextAirScheduleTime());
        anime.setNextAirEp(dto.getNextAirEp());

        if (dto.getTermsByType() != null) {
            if (dto.getTermsByType().getGenre() != null) {
                List<Genre> genres = new ArrayList<>();
                for (String g : dto.getTermsByType().getGenre()) {
                    String genreName = g.trim();
                    if (!genreName.isEmpty()) {
                        Genre genre = genreRepository.findByName(genreName).orElseGet(() -> {
                            Genre newGenre = new Genre();
                            newGenre.setName(genreName);
                            return genreRepository.save(newGenre);
                        });
                        if (!genres.contains(genre)) {
                            genres.add(genre);
                        }
                    }
                }
                anime.setGenresList(genres);
            }
            if (dto.getTermsByType().getProducers() != null) {
                List<Producer> producers = new ArrayList<>();
                for (String p : dto.getTermsByType().getProducers()) {
                    String producerName = p.trim();
                    if (!producerName.isEmpty()) {
                        Producer producer = producerRepository.findByName(producerName).orElseGet(() -> {
                            Producer newProducer = new Producer();
                            newProducer.setName(producerName);
                            return producerRepository.save(newProducer);
                        });
                        if (!producers.contains(producer)) {
                            producers.add(producer);
                        }
                    }
                }
                anime.setProducerList(producers);
            }
            if (dto.getTermsByType().getStudios() != null) {
                List<Studio> studios = new ArrayList<>();
                for (String s : dto.getTermsByType().getStudios()) {
                    String studioName = s.trim();
                    if (!studioName.isEmpty()) {
                        Studio studio = studioRepository.findByName(studioName).orElseGet(() -> {
                            Studio newStudio = new Studio();
                            newStudio.setName(studioName);
                            return studioRepository.save(newStudio);
                        });
                        if (!studios.contains(studio)) {
                            studios.add(studio);
                        }
                    }
                }
                anime.setStudioList(studios);
            }
            if (dto.getTermsByType().getType() != null) {
                anime.setTypes(String.join(",", dto.getTermsByType().getType()));
            }
        }
    }
}
