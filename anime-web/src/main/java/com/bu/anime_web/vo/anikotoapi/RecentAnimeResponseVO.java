package com.bu.anime_web.vo.anikotoapi;

import com.bu.anime_web.vo.common.AnimeVO;
import lombok.Data;

import java.util.List;

@Data
public class RecentAnimeResponseVO {
    private boolean ok;
    private List<String> anikoto_domains;
    private List<AnimeVO> data;
}
