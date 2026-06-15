package com.bu.anime_web.constant;

import lombok.Getter;

@Getter
public enum MailTypeEnum {
    SIGNUP("SIGNUP"),;
    private String type;
    MailTypeEnum(String type) {
        this.type = type;
    }
}
