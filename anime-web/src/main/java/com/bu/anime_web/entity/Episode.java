package com.bu.anime_web.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "episode")
@Data
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_episode_id", referencedColumnName = "id")
    private Anime anime;

    private String title;
    private String jpTitle;
    
    @Column(name = "episode_order")
    private Long order;
    
    private String episodeEmbedId;
    private String updateAt;

    @Embedded
    private EmbedUrl embedUrl;
}
