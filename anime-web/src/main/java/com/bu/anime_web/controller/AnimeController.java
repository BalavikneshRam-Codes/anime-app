package com.bu.anime_web.controller;

import com.bu.anime_web.service.AnimeService;
import com.bu.anime_web.vo.Request.AnimeRequestVO;
import com.bu.anime_web.vo.Request.LoadAnimeRequestVO;
import com.bu.anime_web.vo.Request.RecentAnimeRequestVO;
import com.bu.anime_web.vo.Response.LoadAnimeResponseVO;
import com.bu.anime_web.vo.Response.RecentAnimeResponseVO;
import com.bu.anime_web.vo.common.AnimeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class AnimeController {
    @Autowired
    private AnimeService animeService;
    @PostMapping("/fetchRecentAnimeList")
    public ResponseEntity<RecentAnimeResponseVO> getAnimeList(@RequestBody RecentAnimeRequestVO recentAnimeRequestVO) {
        return ResponseEntity.ok(animeService.fetchRecentAnimeList(recentAnimeRequestVO));
    }
    @PostMapping("/loadAnime")
    public ResponseEntity<LoadAnimeResponseVO> loadAnime(@RequestBody LoadAnimeRequestVO loadAnimeRequestVO) {
        return ResponseEntity.ok(animeService.loadAnime(loadAnimeRequestVO));
    }
    @PostMapping("/fetchAnime")
    public ResponseEntity<AnimeVO> fetchAnime(@RequestBody AnimeRequestVO animeRequestVO) {
        return ResponseEntity.ok(animeService.fetchAnime(animeRequestVO));
    }
    @GetMapping("/heartBeat")
    public ResponseEntity<String> heartBeat() {
        return ResponseEntity.ok("website is live");
    }
}
