package com.bu.anime_web.helper;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AuthHelper {
    public String generateOTP() {
        SecureRandom secureRandom = new SecureRandom();

        // Generate a random number between 0 and 999999 (for a 6-digit OTP)
        int randomNumber = secureRandom.nextInt((int) Math.pow(10, 6));

        // Format the string to ensure it is exactly 6 digits.
        // If the random number is '42', it will be padded with zeros to become '000042'
        return String.format("%0" + 6 + "d", randomNumber);
    }
}
