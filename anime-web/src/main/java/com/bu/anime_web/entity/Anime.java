package com.bu.anime_web.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "anime")
@Data
public class Anime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "anime_anime_id")
    private Long animeId;

    private String title;
    private String alternative;
    
    @Column(columnDefinition = "TEXT")
    private String titles;
    
    private String nativeTitle;
    private String slug;
    private String rating;
    
    private String poster;
    
    private Integer isSub;
    private Integer isDub;

    @Column(columnDefinition = "TEXT")
    private String description;
    @Version
    private long version;
    
    private String aired;
    private String season;
    private Integer year;
    private String duration;
    private String status;
    private String score;
    private String malId;
    private String episodesNum;
    private String aniId;
    private String source;
    
    private String backgroundImage;
    private String updatedAt;

    // Comma-separated strings for lists
    @Column(columnDefinition = "TEXT")
    private String studios;

    @Column(columnDefinition = "TEXT")
    private String genres;

    @Column(columnDefinition = "TEXT")
    private String types;
    @Column(columnDefinition = "TEXT")
    private String producers;
    
    private Long nextAirScheduleTime;
    private Integer nextAirEp;
    @OneToMany(mappedBy = "anime", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Episode> episodesList;
}
