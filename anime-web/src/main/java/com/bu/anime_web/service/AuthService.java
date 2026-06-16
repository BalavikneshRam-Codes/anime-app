package com.bu.anime_web.service;

import com.bu.anime_web.entity.User;
import com.bu.anime_web.repository.IUserRepository;
import com.bu.anime_web.vo.Request.SetPasswordRequestVO;
import com.bu.anime_web.vo.Request.ValidateOtpRequestVO;
import com.bu.anime_web.vo.Request.ValidateOtpResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    @Autowired
    private IUserRepository userRepository;
    public ValidateOtpResponseVO validateOtp(ValidateOtpRequestVO validateOtpRequestVO) {
        ValidateOtpResponseVO validateOtpResponseVO = new ValidateOtpResponseVO();
        try{
            if(validateOtpRequestVO.getEmail() != null && !validateOtpRequestVO.getEmail().isEmpty() && validateOtpRequestVO.getOtp() != null && !validateOtpRequestVO.getOtp().isEmpty()){
                User user = userRepository.findByEmail(validateOtpRequestVO.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found. Please check your email."));

                if (user.getExpiryTime().isBefore(LocalDateTime.now())) {
                    throw new RuntimeException("Your OTP has expired. Please request a new one.");
                }

                if (!user.getOtp().equals(validateOtpRequestVO.getOtp())) {
                    throw new RuntimeException("Invalid OTP. Please check the code and try again.");
                }
                user.setExpiryTime(null);
                user.setOtp(null);
                user.setIsVerified(true);
                userRepository.save(user);
                validateOtpResponseVO.setMessage("Account verified successfully. You can now log in.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return validateOtpResponseVO;
    }

    public ValidateOtpResponseVO setPassword(SetPasswordRequestVO setPasswordRequestVO) {
        ValidateOtpResponseVO validateOtpResponseVO = new ValidateOtpResponseVO();
        try{
            if(setPasswordRequestVO.getEmail() != null && !setPasswordRequestVO.getEmail().isEmpty()){
                if(!setPasswordRequestVO.getNewPassword().equals(setPasswordRequestVO.getConfirmPassword()))
                    throw new RuntimeException("Passwords don't match. Please try again.");
                User user = userRepository.findByEmailAndIsVerifiedTrue(setPasswordRequestVO.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found. Please check your email."));
                user.setPassword(setPasswordRequestVO.getNewPassword());
                userRepository.save(user);
                validateOtpResponseVO.setMessage("Account verified successfully. You can now log in.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return validateOtpResponseVO;
    }
}
