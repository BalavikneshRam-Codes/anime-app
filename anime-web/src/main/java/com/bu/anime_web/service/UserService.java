package com.bu.anime_web.service;

import com.bu.anime_web.bean.SignUpRequestBean;
import com.bu.anime_web.constant.MailTypeEnum;
import com.bu.anime_web.entity.User;
import com.bu.anime_web.helper.AuthHelper;
import com.bu.anime_web.helper.MailHelper;
import com.bu.anime_web.notification.mail.IMailBuilder;
import com.bu.anime_web.notification.mail.factory.MailFactory;
import com.bu.anime_web.repository.IUserRepository;
import com.bu.anime_web.vo.Request.SignUpRequestVO;
import com.bu.anime_web.vo.Response.SignUpResponseVO;
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
    @Autowired
    private AuthHelper authHelper;
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
                signUpUser.setOtp(authHelper.generateOTP());
                signUpUser.setExpiryTime(LocalDateTime.now().plusMinutes(Long.parseLong(environment.getProperty("signUp.validity.minutes"))));
                signUpUser.setIsVerified(false);
                userRepository.save(signUpUser);
                IMailBuilder mailBuilder = mailFactory.getMailBuilder(MailTypeEnum.SIGNUP);
                Thread.startVirtualThread(() -> {
                    try {
                        mailHelper.sendMail(mailBuilder.getMailSenderBean(mailHelper.setSignupBean(signUpRequestVO.getEmail(), signUpUser.getOtp(), signUpUser.getUsername())));
                    } catch (Exception e) {
                        log.error("During signup : "+e.getMessage());
                    }
                });
                signUpResponseVO.setOtpExpireMins(environment.getProperty("signUp.validity.minutes"));
            }
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
        return signUpResponseVO;
    }
}
