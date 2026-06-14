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
}
