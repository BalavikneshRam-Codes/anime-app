package com.bu.anime_web.controller;

import com.bu.anime_web.service.AuthService;
import com.bu.anime_web.vo.Request.*;
import com.bu.anime_web.vo.Response.AuthenticateResponseVO;
import com.bu.anime_web.vo.Response.SignUpResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/validateOtp")
    public ResponseEntity<ValidateOtpResponseVO> validateOtp(@RequestBody ValidateOtpRequestVO validateOtpRequestVO) {
        return ResponseEntity.ok(authService.validateOtp(validateOtpRequestVO));
    }
    @PostMapping("/setPassword")
    public ResponseEntity<ValidateOtpResponseVO> setPassword(@RequestBody SetPasswordRequestVO setPasswordRequestVO) {
        return ResponseEntity.ok(authService.setPassword(setPasswordRequestVO));
    }
    @PostMapping("/resetOtp")
    public ResponseEntity<SignUpResponseVO> resetOtp(@RequestBody SignUpRequestVO signUpRequestVO) {
        return ResponseEntity.ok(authService.resetOtp(signUpRequestVO));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponseVO> authenticate(@RequestBody AuthenticateRequestVO authenticateRequestVO) {
        return ResponseEntity.ok(authService.authenticate(authenticateRequestVO));
    }
}
