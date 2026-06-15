package com.bu.anime_web.bean;

import com.bu.anime_web.vo.common.BaseVO;
import lombok.Data;


@Data
public class SignUpRequestBean extends BaseVO {
    private String otp;
    private String validityMinutes;
    private String email;
    private String username;
}
