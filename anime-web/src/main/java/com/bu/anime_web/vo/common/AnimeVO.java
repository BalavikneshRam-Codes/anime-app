package com.bu.anime_web.vo.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AnimeVO {
    private Long id;
    private String title;
    private String alternative;
    private String titles;

    @JsonProperty("native")
    private String nativeTitle;

    private String slug;
    private String rating;
    private String poster;

    @JsonProperty("is_sub")
    private Integer isSub;

    private String description;
    private String aired;
    private String season;
    private Integer year;
    private String duration;
    private String status;
    private String score;

    @JsonProperty("mal_id")
    private String malId;

    private String episodes;

    @JsonProperty("ani_id")
    private String aniId;

    private String source;

    @JsonProperty("background_image")
    private String backgroundImage;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("terms_by_type")
    private TermsByType termsByType;

    @JsonProperty("next_air_schedule_time")
    private Long nextAirScheduleTime;

    @JsonProperty("next_air_ep")
    private Integer nextAirEp;

    @JsonProperty("episodes_list")
    private java.util.List<EpisodeVO> episodesList;

    @JsonProperty("genres_list")
    private java.util.List<GenreVO> genresList;

    @JsonProperty("producer_list")
    private java.util.List<ProducerVO> producerList;

    @JsonProperty("studio_list")
    private java.util.List<StudioVO> studioList;
    private String latestUserEpisodeId;
    
    @JsonProperty("is_favorite")
    private Boolean isFavorite;
    
    @JsonProperty("watch_status")
    private String watchStatus;
    
    @JsonProperty("watched_episode_ids")
    private java.util.List<String> watchedEpisodeIds;
}
