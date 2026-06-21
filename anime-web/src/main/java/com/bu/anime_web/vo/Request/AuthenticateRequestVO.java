package com.bu.anime_web.vo.Request;

import com.bu.anime_web.vo.common.BaseVO;
import lombok.Data;

@Data
public class AuthenticateRequestVO extends BaseVO {
    private String email;
    private String password;
}
