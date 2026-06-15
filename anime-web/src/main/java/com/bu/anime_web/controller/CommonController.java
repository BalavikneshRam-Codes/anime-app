package com.bu.anime_web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class CommonController {
    @GetMapping("/heartBeat")
    public ResponseEntity<String> heartBeat() {
        return ResponseEntity.ok("website is live");
    }
}
