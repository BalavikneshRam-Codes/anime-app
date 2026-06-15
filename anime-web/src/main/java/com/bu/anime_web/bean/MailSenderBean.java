package com.bu.anime_web.bean;

import com.bu.anime_web.vo.common.AttachmentVO;
import lombok.Data;

import java.util.List;
@Data
public class MailSenderBean {
    private String toEmail;
    private String subject;
    private String body;
    private List<AttachmentVO> attachments;
    private String fileName;
}
