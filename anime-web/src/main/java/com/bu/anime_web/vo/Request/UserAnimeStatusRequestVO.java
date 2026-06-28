package com.bu.anime_web.vo.Request;

import lombok.Data;

@Data
public class UserAnimeStatusRequestVO {
    private Long userId;
    private Long animeId;
    private Boolean isFavorite;
    private String watchStatus;
}
