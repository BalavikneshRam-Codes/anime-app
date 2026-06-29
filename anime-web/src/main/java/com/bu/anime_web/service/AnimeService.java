package com.bu.anime_web.service;

import com.bu.anime_web.converter.AnimeConverter;
import com.bu.anime_web.entity.Anime;
import com.bu.anime_web.entity.UserEpisodeProgress;
import com.bu.anime_web.helper.UserHelper;
import com.bu.anime_web.repository.AnimeCustomRepository;
import com.bu.anime_web.repository.AnimeRepository;
import com.bu.anime_web.repository.UserEpisodeProgressRepository;
import com.bu.anime_web.vo.Request.AnimeRequestVO;
import com.bu.anime_web.vo.Request.CollectionRequestVO;
import com.bu.anime_web.vo.Request.LoadAnimeRequestVO;
import com.bu.anime_web.vo.Request.RecentAnimeRequestVO;
import com.bu.anime_web.vo.Response.LoadAnimeResponseVO;
import com.bu.anime_web.vo.Response.RecentAnimeResponseVO;
import com.bu.anime_web.vo.Response.AnimeFilterResponseVO;
import com.bu.anime_web.vo.Response.UpdateAnimeStatusResponseVO;
import com.bu.anime_web.vo.Request.UserAnimeStatusRequestVO;
import com.bu.anime_web.entity.UserAnimeStatus;
import com.bu.anime_web.entity.WatchStatus;
import com.bu.anime_web.repository.UserAnimeStatusRepository;
import com.bu.anime_web.vo.common.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

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
    @Autowired
    private com.bu.anime_web.repository.GenreRepository genreRepository;
    @Autowired
    private AnimeConverter animeConverter;
    @Autowired
    private UserEpisodeProgressRepository userEpisodeProgressRepository;
    @Autowired
    private UserHelper userHelper;
    @Autowired
    private UserAnimeStatusRepository userAnimeStatusRepository;

    public RecentAnimeResponseVO fetchRecentAnimeList(RecentAnimeRequestVO recentAnimeRequestVO) {
        String pageNumStr = recentAnimeRequestVO != null ? recentAnimeRequestVO.getPageNum() : null;
        String pageSizeStr = recentAnimeRequestVO != null ? recentAnimeRequestVO.getPageSize() : null;
        
        Pageable pageable = com.bu.anime_web.helper.PaginationUtil.getPageable(pageNumStr, pageSizeStr);
        Page<Anime> animePage;
        
        if ("score".equalsIgnoreCase(recentAnimeRequestVO != null ? recentAnimeRequestVO.getSortBy() : null)) {
            animePage = animeRepository.findAnimeSortedByScore(pageable);
        } else {
            animePage = animeRepository.findAnimeByLatestEpisodeUpdate(pageable);
        }

        List<AnimeVO> animeVOList = animePage.getContent().stream()
                .map(animeConverter::mapToAnimeVO)
                .collect(Collectors.toList());
                
        if (recentAnimeRequestVO != null && recentAnimeRequestVO.getUserId() != null) {
            populateUserAnimeStatus(animeVOList, recentAnimeRequestVO.getUserId());
        }

        com.bu.anime_web.vo.common.PageableVO pageableVO = com.bu.anime_web.helper.PaginationUtil.mapToPageableVO(animePage);

        RecentAnimeResponseVO response = new RecentAnimeResponseVO();
        response.setAnimeList(animeVOList);
        response.setPageableVO(pageableVO);
        return response;
    }

    public LoadAnimeResponseVO loadAnime(LoadAnimeRequestVO loadAnimeRequestVO) {
        String pageNumStr = loadAnimeRequestVO != null ? loadAnimeRequestVO.getPageNum() : null;
        String pageSizeStr = loadAnimeRequestVO != null ? loadAnimeRequestVO.getPageSize() : null;

        Pageable pageable = com.bu.anime_web.helper.PaginationUtil.getPageable(pageNumStr, pageSizeStr);
        
        Page<Anime> animePage = animeCustomRepository.findAnimesWithDynamicFilters(loadAnimeRequestVO, pageable);

        List<AnimeVO> animeVOList = animePage.getContent().stream()
                .map(animeConverter::mapToAnimeVO)
                .collect(Collectors.toList());
                
        if (loadAnimeRequestVO != null && loadAnimeRequestVO.getUserId() != null) {
            populateUserAnimeStatus(animeVOList, loadAnimeRequestVO.getUserId());
        }

        LoadAnimeResponseVO response = new LoadAnimeResponseVO();
        response.setAnimeList(animeVOList);

        com.bu.anime_web.vo.common.PageableVO pageableVO = com.bu.anime_web.helper.PaginationUtil.mapToPageableVO(animePage);
        response.setPageableVO(pageableVO);

        return response;
    }

    public AnimeVO fetchAnime(AnimeRequestVO animeRequestVO) {
        if (animeRequestVO == null || animeRequestVO.getAnimeId() == null) {
            throw new IllegalArgumentException("Anime ID must be provided");
        }

        Anime anime = animeRepository.findByAnimeId(animeRequestVO.getAnimeId())
                .orElseThrow(() -> new IllegalArgumentException("Anime not found with ID: " + animeRequestVO.getAnimeId()));

        AnimeVO vo = animeConverter.mapToAnimeVO(anime);
        if(animeRequestVO.getUserId() != null) {
            com.bu.anime_web.entity.User user = userHelper.getUser(animeRequestVO.getUserId());
            userEpisodeProgressRepository.findFirstByEpisodeAnimeAndUserOrderByEpisodeOrderDesc(anime, user).ifPresent(userEpisodeProgress -> {
                vo.setLatestUserEpisodeId(userEpisodeProgress.getEpisode().getEpisodeEmbedId());
            });
            
            List<String> watchedIds = userEpisodeProgressRepository.findByEpisodeAnimeAndUser(anime, user)
                    .stream()
                    .map(p -> p.getEpisode().getEpisodeEmbedId())
                    .collect(Collectors.toList());
            vo.setWatchedEpisodeIds(watchedIds);
        }

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

    public AnimeFilterResponseVO fetchAnimeFilter(BaseVO baseVO) {
        AnimeFilterResponseVO response = new AnimeFilterResponseVO();
        
        // Fetch distinct ratings
        response.setRatings(animeRepository.findAllDistinctRatings().stream()
                .filter(r -> r != null && !r.trim().isEmpty())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList()));
        
        // Fetch distinct types
        List<String> rawTypes = animeRepository.findAllDistinctTypes();
        // Since types might be comma separated in db or single string, we just get distinct directly or split
        // For simplicity we will assume they are simple strings or handle splitting if necessary.
        // Actually types seems to be a list in string form or single string. We'll return it raw or split later if needed.
        response.setTypes(rawTypes.stream()
                .filter(t -> t != null && !t.trim().isEmpty())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList()));

        
        // Fetch distinct statuses
        response.setStatuses(animeRepository.findAllDistinctStatus().stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList()));
        
        // Fetch genres
        List<String> genres = genreRepository.findAll().stream()
                .map(com.bu.anime_web.entity.Genre::getName)
                .filter(g -> g != null && !g.trim().isEmpty())
                .sorted()
                .collect(Collectors.toList());
        response.setGenres(genres);

        return response;
    }

    private void populateUserAnimeStatus(List<AnimeVO> animeVOList, Long userId) {
        if (animeVOList == null || animeVOList.isEmpty()) return;
        List<Long> animeIds = animeVOList.stream().map(AnimeVO::getId).collect(Collectors.toList());
        List<UserAnimeStatus> statuses = userAnimeStatusRepository.findByUserIdAndAnimeIdIn(userId, animeIds);
        
        java.util.Map<Long, UserAnimeStatus> statusMap = statuses.stream()
                .collect(Collectors.toMap(s -> s.getAnime().getId(), s -> s));
                
        for (AnimeVO vo : animeVOList) {
            UserAnimeStatus status = statusMap.get(vo.getId());
            if (status != null) {
                vo.setIsFavorite(status.getIsFavorite());
                vo.setWatchStatus(status.getWatchStatus() != null ? status.getWatchStatus().name() : null);
            }
        }
    }

    public UpdateAnimeStatusResponseVO updateUserAnimeStatus(UserAnimeStatusRequestVO request) {
        UpdateAnimeStatusResponseVO response = new UpdateAnimeStatusResponseVO();
        try {
            if (request.getUserId() == null || request.getAnimeId() == null) {
                throw new IllegalArgumentException("User ID and Anime ID are required");
            }

            com.bu.anime_web.entity.User user = userHelper.getUser(request.getUserId());
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }

            Anime anime = animeRepository.findById(request.getAnimeId())
                    .orElseThrow(() -> new IllegalArgumentException("Anime not found"));

            UserAnimeStatus status = userAnimeStatusRepository.findByUserIdAndAnimeId(request.getUserId(), request.getAnimeId())
                    .orElse(new UserAnimeStatus());

            if (status.getId() == null) {
                status.setUser(user);
                status.setAnime(anime);
            }

            if (request.getIsFavorite() != null) {
                status.setIsFavorite(request.getIsFavorite());
            }

            if (request.getWatchStatus() != null) {
                status.setWatchStatus(WatchStatus.valueOf(request.getWatchStatus()));
            }

            userAnimeStatusRepository.save(status);

            response.setStatus("success");
            response.setMessage("Anime status updated successfully");
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public RecentAnimeResponseVO fetchUserCollection(CollectionRequestVO request) {
        if (request == null || request.getUserId() == null || request.getCollectionType() == null) {
            throw new IllegalArgumentException("Invalid collection request");
        }

        int pageNum = request.getPageNum() != null ? Integer.parseInt(request.getPageNum()) : 1;
        int pageSize = request.getPageSize() != null ? Integer.parseInt(request.getPageSize()) : 20;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageNum - 1, 
                pageSize, 
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "updatedAt")
        );

        org.springframework.data.domain.Page<UserAnimeStatus> statusPage;
        if ("favorites".equalsIgnoreCase(request.getCollectionType())) {
            statusPage = userAnimeStatusRepository.findByUserIdAndIsFavoriteTrue(request.getUserId(), pageable);
        } else if ("bookmarks".equalsIgnoreCase(request.getCollectionType())) {
            statusPage = userAnimeStatusRepository.findByUserIdAndWatchStatus(request.getUserId(), WatchStatus.WATCH_LATER, pageable);
        } else {
            throw new IllegalArgumentException("Unknown collection type");
        }

        List<AnimeVO> animeVOs = statusPage.getContent().stream()
                .map(status -> {
                    AnimeVO vo = animeConverter.mapToAnimeVO(status.getAnime());
                    vo.setIsFavorite(status.getIsFavorite());
                    vo.setWatchStatus(status.getWatchStatus() != null ? status.getWatchStatus().name() : null);
                    return vo;
                })
                .collect(Collectors.toList());

        com.bu.anime_web.vo.common.PageableVO pageableVO = new com.bu.anime_web.vo.common.PageableVO();
        pageableVO.setPageNumber(pageNum);
        pageableVO.setPageSize(pageSize);
        pageableVO.setTotalElements((int) statusPage.getTotalElements());
        pageableVO.setTotalPages(statusPage.getTotalPages());
        pageableVO.setLast(statusPage.isLast());

        RecentAnimeResponseVO response = new RecentAnimeResponseVO();
        response.setAnimeList(animeVOs);
        response.setPageableVO(pageableVO);

        return response;
    }
}
