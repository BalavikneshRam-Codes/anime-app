package com.bu.anime_web.service;

import com.bu.anime_web.constant.MailTypeEnum;
import com.bu.anime_web.entity.User;
import com.bu.anime_web.helper.AuthHelper;
import com.bu.anime_web.helper.MailHelper;
import com.bu.anime_web.notification.mail.IMailBuilder;
import com.bu.anime_web.notification.mail.factory.MailFactory;
import com.bu.anime_web.repository.IUserRepository;
import com.bu.anime_web.vo.Request.SetPasswordRequestVO;
import com.bu.anime_web.vo.Request.SignUpRequestVO;
import com.bu.anime_web.vo.Request.ValidateOtpRequestVO;
import com.bu.anime_web.vo.Request.ValidateOtpResponseVO;
import com.bu.anime_web.vo.Response.SignUpResponseVO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private AuthHelper authHelper;
    @Autowired
    private Environment environment;
    @Autowired
    private MailFactory mailFactory;
    @Autowired
    private MailHelper mailHelper;
    public ValidateOtpResponseVO validateOtp(ValidateOtpRequestVO validateOtpRequestVO) {
        ValidateOtpResponseVO validateOtpResponseVO = new ValidateOtpResponseVO();
        try{
            if(validateOtpRequestVO.getEmail() != null && !validateOtpRequestVO.getEmail().isEmpty() && validateOtpRequestVO.getOtp() != null && !validateOtpRequestVO.getOtp().isEmpty()){
                User user = userRepository.findByEmail(validateOtpRequestVO.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found. Please check your email."));

                if (user.getExpiryTime().isBefore(LocalDateTime.now()))
                    throw new RuntimeException("Your OTP has expired. Please request a new one.");

                if (!user.getOtp().equals(validateOtpRequestVO.getOtp()))
                    throw new RuntimeException("Invalid OTP. Please check the code and try again.");
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
                User user = userRepository.findByEmailAndIsVerified(setPasswordRequestVO.getEmail(),true)
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
    @Transactional
    public SignUpResponseVO resetOtp(SignUpRequestVO setPasswordRequestVO) {
        SignUpResponseVO signUpResponseVO = new SignUpResponseVO();
        if(setPasswordRequestVO.getEmail() != null && !setPasswordRequestVO.getEmail().isEmpty()){
           User user = userRepository.findByEmailAndIsVerified(setPasswordRequestVO.getEmail(),false)
                   .orElseThrow(() -> new RuntimeException("User not found. Please check your email."));
            user.setOtp(authHelper.generateOTP());
            user.setExpiryTime(LocalDateTime.now().plusMinutes(Long.parseLong(environment.getProperty("signUp.validity.minutes"))));
            userRepository.save(user);
            IMailBuilder mailBuilder = mailFactory.getMailBuilder(MailTypeEnum.SIGNUP);
            Thread.startVirtualThread(() -> {
                try {
                    mailHelper.sendMail(mailBuilder.getMailSenderBean(mailHelper.setSignupBean(user.getEmail(), user.getOtp(), user.getUsername())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            signUpResponseVO.setOtpExpireMins(environment.getProperty("signUp.validity.minutes"));
        }
        return signUpResponseVO;
    }
}
