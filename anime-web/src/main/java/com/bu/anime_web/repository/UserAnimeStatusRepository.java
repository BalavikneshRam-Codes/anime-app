package com.bu.anime_web.repository;

import com.bu.anime_web.entity.UserAnimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAnimeStatusRepository extends JpaRepository<UserAnimeStatus, Long> {
    Optional<UserAnimeStatus> findByUserIdAndAnimeId(Long userId, Long animeId);
    java.util.List<UserAnimeStatus> findByUserIdAndAnimeIdIn(Long userId, java.util.Collection<Long> animeIds);
    org.springframework.data.domain.Page<UserAnimeStatus> findByUserIdAndIsFavoriteTrue(Long userId, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<UserAnimeStatus> findByUserIdAndWatchStatus(Long userId, com.bu.anime_web.entity.WatchStatus watchStatus, org.springframework.data.domain.Pageable pageable);
}
