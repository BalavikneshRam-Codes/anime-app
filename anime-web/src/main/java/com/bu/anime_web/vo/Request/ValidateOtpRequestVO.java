package com.bu.anime_web.vo.Request;

import com.bu.anime_web.vo.common.BaseVO;
import lombok.Data;

@Data
public class ValidateOtpRequestVO extends BaseVO {
    private String otp;
    private String email;
}
