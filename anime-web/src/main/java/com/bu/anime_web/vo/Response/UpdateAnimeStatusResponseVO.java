package com.bu.anime_web.vo.Response;

import com.bu.anime_web.vo.common.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateAnimeStatusResponseVO extends BaseVO {
    private String status;
    private String message;
}
