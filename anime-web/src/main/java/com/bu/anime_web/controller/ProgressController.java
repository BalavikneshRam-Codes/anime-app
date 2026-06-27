package com.bu.anime_web.controller;

import com.bu.anime_web.service.ProgressService;
import com.bu.anime_web.vo.Request.MarkEpisodeProgressRequestVO;
import com.bu.anime_web.vo.Response.MarkEpisodeProgressResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "*")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @PostMapping("/complete")
    public ResponseEntity<MarkEpisodeProgressResponseVO> markCompleted(@RequestBody MarkEpisodeProgressRequestVO request) {
        MarkEpisodeProgressResponseVO response = progressService.markCompleted(request);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
