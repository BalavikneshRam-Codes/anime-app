package com.bu.anime_web.vo.Response;

import com.bu.anime_web.vo.common.BaseVO;
import com.bu.anime_web.vo.common.UserVO;
import lombok.Data;

@Data
public class AuthenticateResponseVO extends BaseVO {
    private UserVO userVO;
}
