package com.bu.anime_web.repository;

import com.bu.anime_web.entity.Anime;
import com.bu.anime_web.entity.User;
import com.bu.anime_web.entity.UserEpisodeProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEpisodeProgressRepository extends JpaRepository<UserEpisodeProgress, Long> {
    Optional<UserEpisodeProgress> findByUserIdAndEpisodeId(Long userId, Long episodeId);
    Optional<UserEpisodeProgress> findFirstByEpisodeAnimeAndUserOrderByEpisodeOrderDesc(Anime anime, User user);
    java.util.List<UserEpisodeProgress> findByEpisodeAnimeAndUser(Anime anime, User user);
}
