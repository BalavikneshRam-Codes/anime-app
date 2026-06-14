package com.bu.anime_web.vo.Response;

import com.bu.anime_web.vo.common.AnimeVO;
import lombok.Data;

import java.util.List;

@Data
public class RecentAnimeResponseVO {
    private List<AnimeVO> animeList;
}
