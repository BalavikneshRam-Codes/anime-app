package com.bu.anime_web.notification.mail;

import com.bu.anime_web.bean.MailSenderBean;
import com.bu.anime_web.vo.common.BaseVO;

public interface IMailBuilder {
    MailSenderBean getMailSenderBean(BaseVO baseVO);
}
