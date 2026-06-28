package com.bu.anime_web.vo.Request;

import lombok.Data;

@Data
public class LoadAnimeRequestVO {
    private Long userId;
    private String title;
    private String type;
    private String genres;
    private String season;
    private String rating;
    private String status;
    private String pageNum;
    private String pageSize;
}
