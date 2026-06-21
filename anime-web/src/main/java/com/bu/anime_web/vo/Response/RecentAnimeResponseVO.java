package com.bu.anime_web.vo.Response;

import com.bu.anime_web.vo.common.AnimeVO;
import com.bu.anime_web.vo.common.PageableVO;
import lombok.Data;

import java.util.List;

@Data
public class RecentAnimeResponseVO {
    private List<AnimeVO> animeList;
    private PageableVO pageableVO;
}
