package com.bu.anime_web.vo.Response;

import lombok.Data;

import java.util.List;

@Data
public class AnimeFilterResponseVO {
    private List<String> ratings;
    private List<String> genres;
    private List<String> seasons;
    private List<String> types;
    private List<String> statuses;
}
