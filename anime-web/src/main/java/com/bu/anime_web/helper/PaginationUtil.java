package com.bu.anime_web.helper;

import com.bu.anime_web.vo.common.PageableVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationUtil {

    public static Pageable getPageable(String pageNumStr, String pageSizeStr) {
        int pageNum = 1;
        int pageSize = 20;

        try {
            if (pageNumStr != null && !pageNumStr.isEmpty()) {
                pageNum = Integer.parseInt(pageNumStr);
            }
            if (pageSizeStr != null && !pageSizeStr.isEmpty()) {
                pageSize = Integer.parseInt(pageSizeStr);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid pageNum or pageSize format. They must be integers.");
        }

        return PageRequest.of(Math.max(0, pageNum - 1), pageSize);
    }

    public static PageableVO mapToPageableVO(Page<?> page) {
        PageableVO pageableVO = new PageableVO();
        pageableVO.setPageNumber(page.getNumber() + 1);
        pageableVO.setPageSize(page.getSize());
        pageableVO.setTotalElements(page.getTotalElements());
        pageableVO.setTotalPages(page.getTotalPages());
        pageableVO.setFirst(page.isFirst());
        pageableVO.setLast(page.isLast());
        return pageableVO;
    }
}
