package com.bu.anime_web.notification.mail.impl;

import com.bu.anime_web.bean.MailSenderBean;
import com.bu.anime_web.bean.SignUpRequestBean;
import com.bu.anime_web.notification.mail.IMailBuilder;
import com.bu.anime_web.vo.Request.SignUpRequestVO;
import com.bu.anime_web.vo.common.BaseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class SignUpMailBuilder implements IMailBuilder {
    @Autowired
    private TemplateEngine templateEngine;
    @Override
    public MailSenderBean getMailSenderBean(BaseVO baseVO) {
        MailSenderBean mailSenderBean = null;
        try{
            if(baseVO instanceof SignUpRequestBean signUpRequestBean){
                mailSenderBean = new MailSenderBean();
                mailSenderBean.setToEmail(signUpRequestBean.getEmail());
                mailSenderBean.setSubject("Anime-Web");
                Context context = new Context();
                context.setVariable("otp", signUpRequestBean.getOtp());
                context.setVariable("validityMinutes", signUpRequestBean.getValidityMinutes());
                context.setVariable("username", signUpRequestBean.getUsername());
                String htmlBody = templateEngine.process("sigup-email-template", context);
                mailSenderBean.setBody(htmlBody);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mailSenderBean;
    }
}
