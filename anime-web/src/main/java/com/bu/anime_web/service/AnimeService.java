package com.bu.anime_web.service;

import com.bu.anime_web.entity.Anime;
import com.bu.anime_web.repository.AnimeCustomRepository;
import com.bu.anime_web.repository.AnimeRepository;
import com.bu.anime_web.vo.Request.AnimeRequestVO;
import com.bu.anime_web.vo.Request.LoadAnimeRequestVO;
import com.bu.anime_web.vo.Request.RecentAnimeRequestVO;
import com.bu.anime_web.vo.Response.LoadAnimeResponseVO;
import com.bu.anime_web.vo.Response.RecentAnimeResponseVO;
import com.bu.anime_web.vo.common.AnimeVO;
import com.bu.anime_web.vo.common.EpisodeVO;
import com.bu.anime_web.vo.common.TermsByType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimeService {

    @Autowired
    private AnimeCustomRepository animeCustomRepository;
    @Autowired
    private AnimeRepository animeRepository;

    public RecentAnimeResponseVO fetchRecentAnimeList(RecentAnimeRequestVO recentAnimeRequestVO) {
        int pageNum = 1;
        int pageSize = 20;

        if (recentAnimeRequestVO != null) {
            try {
                if (recentAnimeRequestVO.getPageNum() != null && !recentAnimeRequestVO.getPageNum().isEmpty()) {
                    pageNum = Integer.parseInt(recentAnimeRequestVO.getPageNum());
                }
                if (recentAnimeRequestVO.getPageSize() != null && !recentAnimeRequestVO.getPageSize().isEmpty()) {
                    pageSize = Integer.parseInt(recentAnimeRequestVO.getPageSize());
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid pageNum or pageSize format. They must be integers.");
            }
        }

        Pageable pageable = PageRequest.of(Math.max(0, pageNum - 1), pageSize);
        Page<Anime> animePage = animeRepository.findAnimeByLatestEpisodeUpdate(pageable);

        List<AnimeVO> animeVOList = animePage.getContent().stream()
                .map(this::mapToAnimeVO)
                .collect(Collectors.toList());

        RecentAnimeResponseVO response = new RecentAnimeResponseVO();
        response.setAnimeList(animeVOList);
        return response;
    }

    public LoadAnimeResponseVO loadAnime(LoadAnimeRequestVO loadAnimeRequestVO) {
        int pageNum = 1;
        int pageSize = 20;

        if (loadAnimeRequestVO != null) {
            try {
                if (loadAnimeRequestVO.getPageNum() != null && !loadAnimeRequestVO.getPageNum().isEmpty()) {
                    pageNum = Integer.parseInt(loadAnimeRequestVO.getPageNum());
                }
                if (loadAnimeRequestVO.getPageSize() != null && !loadAnimeRequestVO.getPageSize().isEmpty()) {
                    pageSize = Integer.parseInt(loadAnimeRequestVO.getPageSize());
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid pageNum or pageSize format. They must be integers.");
            }
        }

        Pageable pageable = PageRequest.of(Math.max(0, pageNum - 1), pageSize);
        
        Page<Anime> animePage = animeCustomRepository.findAnimesWithDynamicFilters(loadAnimeRequestVO, pageable);

        List<AnimeVO> animeVOList = animePage.getContent().stream()
                .map(this::mapToAnimeVO)
                .collect(Collectors.toList());

        LoadAnimeResponseVO response = new LoadAnimeResponseVO();
        response.setAnimeList(animeVOList);
        return response;
    }

    private AnimeVO mapToAnimeVO(Anime anime) {
        AnimeVO vo = new AnimeVO();
        BeanUtils.copyProperties(anime, vo);
        
        if (anime.getAnimeId() != null) {
            vo.setId(anime.getAnimeId());
        }
        
        vo.setEpisodes(anime.getEpisodesNum());
        
        TermsByType terms = new TermsByType();
        if (anime.getStudios() != null && !anime.getStudios().isEmpty()) {
            terms.setStudios(Arrays.asList(anime.getStudios().split(",")));
        }
        if (anime.getGenres() != null && !anime.getGenres().isEmpty()) {
            terms.setGenre(Arrays.asList(anime.getGenres().split(",")));
        }
        if (anime.getTypes() != null && !anime.getTypes().isEmpty()) {
            terms.setType(Arrays.asList(anime.getTypes().split(",")));
        }
        if (anime.getProducers() != null && !anime.getProducers().isEmpty()) {
            terms.setProducers(Arrays.asList(anime.getProducers().split(",")));
        }
        vo.setTermsByType(terms);

        return vo;
    }

    public AnimeVO fetchAnime(AnimeRequestVO animeRequestVO) {
        if (animeRequestVO == null || animeRequestVO.getAnimeId() == null) {
            throw new IllegalArgumentException("Anime ID must be provided");
        }

        Anime anime = animeRepository.findByAnimeId(animeRequestVO.getAnimeId())
                .orElseThrow(() -> new IllegalArgumentException("Anime not found with ID: " + animeRequestVO.getAnimeId()));

        AnimeVO vo = mapToAnimeVO(anime);

        if (anime.getEpisodesList() != null && !anime.getEpisodesList().isEmpty()) {
            List<com.bu.anime_web.vo.common.EpisodeVO> episodeVOList = anime.getEpisodesList().stream().map(ep -> {
                com.bu.anime_web.vo.common.EpisodeVO epVO = new com.bu.anime_web.vo.common.EpisodeVO();
                epVO.setId(ep.getId());
                epVO.setTitle(ep.getTitle());
                epVO.setJpTitle(ep.getJpTitle());
                epVO.setOrder(ep.getOrder());
                epVO.setEpisodeEmbedId(ep.getEpisodeEmbedId());
                epVO.setUpdateAt(ep.getUpdateAt());
                
                if (ep.getEmbedUrl() != null) {
                    epVO.setSubUrl(ep.getEmbedUrl().getSub());
                    epVO.setDubUrl(ep.getEmbedUrl().getDub());
                }
                return epVO;
            }).sorted(Comparator.comparing(EpisodeVO::getOrder,Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
            
            vo.setEpisodesList(episodeVOList);
        }

        return vo;
    }
}
