package com.bu.anime_web.repository;

import com.bu.anime_web.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    List<Episode> findByAnimeAnimeId(Long animeId);
    Optional<Episode> findByAnimeAnimeIdAndEpisodeEmbedId(Long animeId, String episodeEmbedId);
}
