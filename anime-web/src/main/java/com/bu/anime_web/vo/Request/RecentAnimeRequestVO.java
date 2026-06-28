package com.bu.anime_web.vo.Request;

import lombok.Data;

@Data
public class RecentAnimeRequestVO {
    private Long userId;
    private String pageNum;
    private String pageSize;
}
