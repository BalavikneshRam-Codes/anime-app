package com.bu.anime_web.vo.Request;

import lombok.Data;

@Data
public class MarkEpisodeProgressRequestVO {
    private Long userId;
    private Long episodeId;
}
