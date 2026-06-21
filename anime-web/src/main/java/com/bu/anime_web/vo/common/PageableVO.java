package com.bu.anime_web.vo.common;

import lombok.Data;

@Data
public class PageableVO {
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
    private boolean isFirst;
}
