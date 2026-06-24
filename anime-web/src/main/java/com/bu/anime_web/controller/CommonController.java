package com.bu.anime_web.controller;

import com.bu.anime_web.service.AnimeSeriesService;
import com.bu.anime_web.vo.Request.AuthenticateRequestVO;
import com.bu.anime_web.vo.Response.AuthenticateResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class CommonController {
    @Autowired
    private AnimeSeriesService  animeSeriesService;
    @GetMapping("/heartBeat")
    public ResponseEntity<String> heartBeat() {
        return ResponseEntity.ok("website is live");
    }
    @PostMapping("/updateAllAnime")
    public ResponseEntity<String> updateAllAnime(@RequestBody AuthenticateRequestVO authenticateRequestVO) {
        animeSeriesService.updateAllAnime();
        return ResponseEntity.ok("Completed");
    }
}
