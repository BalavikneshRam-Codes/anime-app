package com.bu.anime_web.vo.Request;

import lombok.Data;

@Data
public class CollectionRequestVO {
    private Long userId;
    private String collectionType; // "favorites" or "bookmarks"
    private String pageNum;
    private String pageSize;
}
