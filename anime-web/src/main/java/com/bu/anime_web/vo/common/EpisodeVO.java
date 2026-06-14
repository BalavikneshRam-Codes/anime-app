package com.bu.anime_web.vo.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EpisodeVO {
    private Long id;
    private String title;
    
    @JsonProperty("jp_title")
    private String jpTitle;
    
    private Long order;
    
    @JsonProperty("episode_embed_id")
    private String episodeEmbedId;
    
    @JsonProperty("updated_at")
    private String updateAt;
    
    @JsonProperty("sub_url")
    private String subUrl;
    
    @JsonProperty("dub_url")
    private String dubUrl;
}
