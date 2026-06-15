package com.bu.anime_web.service;

import com.bu.anime_web.bean.SignUpRequestBean;
import com.bu.anime_web.constant.MailTypeEnum;
import com.bu.anime_web.entity.User;
import com.bu.anime_web.helper.MailHelper;
import com.bu.anime_web.notification.mail.IMailBuilder;
import com.bu.anime_web.notification.mail.factory.MailFactory;
import com.bu.anime_web.repository.IUserRepository;
import com.bu.anime_web.vo.Request.SignUpRequestVO;
import com.bu.anime_web.vo.Response.SignUpResponseVO;
import com.bu.anime_web.vo.common.BaseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private MailFactory mailFactory;
    @Autowired
    private MailHelper  mailHelper;
    @Autowired
    private Environment environment;
    public SignUpResponseVO signUp(SignUpRequestVO signUpRequestVO) {
        SignUpResponseVO signUpResponseVO = new SignUpResponseVO();
        try {
            if(signUpRequestVO.getEmail() != null && !signUpRequestVO.getEmail().isEmpty() && signUpRequestVO.getUsername() != null && !signUpRequestVO.getUsername().isEmpty()) {
                userRepository.findByEmail(signUpRequestVO.getEmail()).ifPresent(_ -> {
                    throw new RuntimeException("Email already exists");
                });
                User signUpUser = new User();
                signUpUser.setUsername(signUpRequestVO.getUsername());
                signUpUser.setEmail(signUpRequestVO.getEmail());
                signUpUser.setOtp(generateOTP());
                signUpUser.setExpiryTime(LocalDateTime.now());
                userRepository.save(signUpUser);
                IMailBuilder mailBuilder = mailFactory.getMailBuilder(MailTypeEnum.SIGNUP);
                Thread.startVirtualThread(() -> {
                    try {
                        mailHelper.sendMail(mailBuilder.getMailSenderBean(setSignupBean(signUpRequestVO.getEmail(), signUpUser.getOtp(), signUpUser.getUsername())));
                    } catch (Exception e) {
                        log.error("During signup : "+e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
        return signUpResponseVO;
    }

    private SignUpRequestBean setSignupBean(String email,String otp,String username) {
        SignUpRequestBean signUpRequestBean = new SignUpRequestBean();
        signUpRequestBean.setEmail(email);
        signUpRequestBean.setOtp(otp);
        signUpRequestBean.setUsername(username);
        signUpRequestBean.setValidityMinutes(environment.getProperty("signUp.validity.minutes"));
        return signUpRequestBean;
    }

    public String generateOTP() {
        SecureRandom secureRandom = new SecureRandom();

        // Generate a random number between 0 and 999999 (for a 6-digit OTP)
        int randomNumber = secureRandom.nextInt((int) Math.pow(10, 6));

        // Format the string to ensure it is exactly 6 digits.
        // If the random number is '42', it will be padded with zeros to become '000042'
        return String.format("%0" + 6 + "d", randomNumber);
    }
}
