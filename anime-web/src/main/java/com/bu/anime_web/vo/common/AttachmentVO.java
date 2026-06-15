package com.bu.anime_web.vo.common;

import lombok.Data;

@Data
public class AttachmentVO extends BaseVO {
    private String fileName;
    private byte[] multipartBytes;
}