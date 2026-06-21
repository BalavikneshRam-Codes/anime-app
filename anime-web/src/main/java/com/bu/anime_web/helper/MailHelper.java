package com.bu.anime_web.helper;

import com.bu.anime_web.bean.MailSenderBean;
import com.bu.anime_web.bean.SignUpRequestBean;
import com.bu.anime_web.constant.MailTypeEnum;
import com.bu.anime_web.notification.mail.factory.MailFactory;
import com.bu.anime_web.vo.common.AttachmentVO;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MailHelper {
    private static final Logger log = LoggerFactory.getLogger(MailHelper.class);
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Environment env;
    @Autowired
    private MailFactory mailFactory;

    public void sendMail(MailSenderBean mailSenderBean) {
        try {
            if (mailSenderBean != null) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(mailSenderBean.getToEmail());
                helper.setFrom(env.getProperty("spring.mail.from"));
                helper.setSubject(mailSenderBean.getSubject());
                helper.setText(mailSenderBean.getBody(), true);
                if (mailSenderBean.getAttachments() != null && !mailSenderBean.getAttachments().isEmpty())
                    for (AttachmentVO attachment : mailSenderBean.getAttachments())
                        if (attachment.getMultipartBytes() != null && attachment.getFileName() != null)
                            helper.addAttachment(attachment.getFileName(), new ByteArrayResource(attachment.getMultipartBytes()));
                mailSender.send(message);
            }
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
        }
    }
    public SignUpRequestBean setSignupBean(String email, String otp, String username) {
        SignUpRequestBean signUpRequestBean = new SignUpRequestBean();
        signUpRequestBean.setEmail(email);
        signUpRequestBean.setOtp(otp);
        signUpRequestBean.setUsername(username);
        signUpRequestBean.setValidityMinutes(env.getProperty("signUp.validity.minutes"));
        return signUpRequestBean;
    }

    public void sendAsyncSignupMail(String email, String otp, String username, MailTypeEnum mailTypeEnum) {
        com.bu.anime_web.notification.mail.IMailBuilder mailBuilder = mailFactory.getMailBuilder(mailTypeEnum);
        Thread.startVirtualThread(() -> {
            try {
                sendMail(mailBuilder.getMailSenderBean(setSignupBean(email, otp, username)));
            } catch (Exception e) {
                log.error("Error during async signup email: " + e.getMessage(), e);
            }
        });
    }
}