package com.bu.anime_web.repository;

import com.bu.anime_web.entity.Anime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    Optional<Anime> findByAnimeId(Long id);

    @Query("SELECT a FROM Anime a LEFT JOIN a.episodesList e GROUP BY a ORDER BY MAX(CAST(e.updateAt AS timestamp)) DESC NULLS LAST")
    Page<Anime> findAnimeByLatestEpisodeUpdate(Pageable pageable);

    @Query("SELECT a FROM Anime a ORDER BY CAST(NULLIF(a.score, '') AS double) DESC NULLS LAST")
    Page<Anime> findAnimeSortedByScore(Pageable pageable);

    @Query("SELECT DISTINCT a.rating FROM Anime a WHERE a.rating IS NOT NULL")
    List<String> findAllDistinctRatings();

    @Query("SELECT DISTINCT a.types FROM Anime a WHERE a.types IS NOT NULL")
    List<String> findAllDistinctTypes();

    @Query("SELECT DISTINCT a.season FROM Anime a WHERE a.season IS NOT NULL")
    List<String> findAllDistinctSeasons();

    @Query("SELECT DISTINCT a.status FROM Anime a WHERE a.status IS NOT NULL")
    List<String> findAllDistinctStatus();
}
