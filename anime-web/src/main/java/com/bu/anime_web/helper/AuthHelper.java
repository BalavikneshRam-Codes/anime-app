package com.bu.anime_web.helper;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Component
public class AuthHelper {
    @Autowired
    private Environment environment;

    public String generateOTP() {
        SecureRandom secureRandom = new SecureRandom();
        int randomNumber = secureRandom.nextInt((int) Math.pow(10, 6));
        return String.format("%0" + 6 + "d", randomNumber);
    }

    public LocalDateTime getExpiryTime() {
        String minutesStr = environment.getProperty("signUp.validity.minutes");
        long minutes = minutesStr != null ? Long.parseLong(minutesStr) : 10L; // default 10
        return LocalDateTime.now().plusMinutes(minutes);
    }
}
