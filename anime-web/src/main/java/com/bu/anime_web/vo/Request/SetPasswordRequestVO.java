package com.bu.anime_web.vo.Request;

import com.bu.anime_web.vo.common.BaseVO;
import lombok.Data;

@Data
public class SetPasswordRequestVO extends BaseVO {
    private String email;
    private String newPassword;
    private String confirmPassword;
}
