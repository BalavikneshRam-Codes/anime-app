package com.bu.anime_web.controller;

import com.bu.anime_web.service.UserService;
import com.bu.anime_web.vo.Request.SignUpRequestVO;
import com.bu.anime_web.vo.Response.SignUpResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponseVO> signUp(@RequestBody SignUpRequestVO signUpRequestVO) {
        return ResponseEntity.ok(userService.signUp(signUpRequestVO));
    }
}
