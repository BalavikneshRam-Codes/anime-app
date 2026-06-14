package com.bu.anime_web.vo.anikotoapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EpisodeVO {
    private Long id;
    private String title;
    
    @JsonProperty("jp_title")
    private String jpTitle;
    
    private Float number;
    
    @JsonProperty("episode_embed_id")
    private String episodeEmbedId;
    
    @JsonProperty("embed_url")
    private EmbedUrlVO embedUrl;
    
    @JsonProperty("updated_at")
    private String updatedAt;
}
