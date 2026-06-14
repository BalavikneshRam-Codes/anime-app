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
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "anime_studio", // The name of the new junction table in TiDB
            joinColumns = @JoinColumn(name = "anime_id"),
            inverseJoinColumns = @JoinColumn(name = "studio_id")
    )
    private List<Studio> studioList;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "anime_genres", // The name of the new junction table in TiDB
            joinColumns = @JoinColumn(name = "anime_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genresList;

    @Column(columnDefinition = "TEXT")
    private String types;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "anime_producer", // The name of the new junction table in TiDB
            joinColumns = @JoinColumn(name = "anime_id"),
            inverseJoinColumns = @JoinColumn(name = "producer_id")
    )
    private List<Producer> producerList;
    private Long nextAirScheduleTime;
    private Integer nextAirEp;
    @OneToMany(mappedBy = "anime", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Episode> episodesList;
}
