package com.bu.anime_web.vo.Response;

import com.bu.anime_web.vo.common.BaseVO;
import lombok.Data;

@Data
public class SignUpResponseVO extends BaseVO {
    private String otpExpireMins;
}
