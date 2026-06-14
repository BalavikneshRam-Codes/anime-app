package com.bu.anime_web.repository;

import com.bu.anime_web.entity.Anime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    Optional<Anime> findByAnimeId(Long id);

    @Query("SELECT a FROM Anime a JOIN a.episodesList e GROUP BY a ORDER BY MAX(CAST(e.updateAt AS timestamp)) DESC")
    Page<Anime> findAnimeByLatestEpisodeUpdate(Pageable pageable);
}
