package com.bu.anime_web.vo.anikotoapi;

import lombok.Data;
import java.util.List;

@Data
public class SeriesResponseVO {
    private boolean ok;
    private List<String> anikoto_domains;
    private SeriesDataVO data;
}
