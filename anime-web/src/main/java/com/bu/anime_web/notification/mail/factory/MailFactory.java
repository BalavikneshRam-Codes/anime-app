package com.bu.anime_web.notification.mail.factory;

import com.bu.anime_web.constant.MailTypeEnum;
import com.bu.anime_web.notification.mail.IMailBuilder;
import com.bu.anime_web.notification.mail.impl.SignUpMailBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailFactory {
    @Autowired
    private SignUpMailBuilder signUpMailBuilder;

    public IMailBuilder getMailBuilder(MailTypeEnum mailType) {
        return switch (mailType) {
            case MailTypeEnum.SIGNUP -> signUpMailBuilder;
            default -> throw new IllegalArgumentException("Invalid mail type: " + mailType);
        };
    }
}