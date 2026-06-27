package com.bu.anime_web.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_episode_progress")
@Data
public class UserEpisodeProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", referencedColumnName = "id")
    private Episode episode;

    private LocalDateTime watchedAt;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.watchedAt = LocalDateTime.now();
    }
}
