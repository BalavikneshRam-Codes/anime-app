package com.bu.anime_web.repository;

import com.bu.anime_web.entity.Anime;
import com.bu.anime_web.vo.Request.LoadAnimeRequestVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnimeCustomRepository {
    Page<Anime> findAnimesWithDynamicFilters(LoadAnimeRequestVO request, Pageable pageable);
}
