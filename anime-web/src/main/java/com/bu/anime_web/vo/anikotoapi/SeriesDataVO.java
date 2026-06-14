package com.bu.anime_web.vo.anikotoapi;

import com.bu.anime_web.vo.common.AnimeVO;
import lombok.Data;
import java.util.List;

@Data
public class SeriesDataVO {
    private AnimeVO anime;
    private List<EpisodeVO> episodes;
}
