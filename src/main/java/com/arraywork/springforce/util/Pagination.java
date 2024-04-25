package com.arraywork.springforce.util;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Data;

/**
 * 分页对象
 * @author AiChen
 * @created 2024/03/10
 */
@Data
public class Pagination<T> {

    private long totalRecords;
    private int totalPages;
    private int pageNumber;
    private boolean hasPrevious;
    private boolean hasNext;
    private boolean hasContent;
    private List<T> content;

    public Pagination(Page<T> page) {
        setTotalRecords(page.getTotalElements());
        setTotalPages(page.getTotalPages());
        setPageNumber(page.getNumber() + 1);
        setHasPrevious(page.hasPrevious());
        setHasNext(page.hasNext());
        setHasContent(page.hasContent());
        setContent(page.getContent());
    }

    public List<Integer> getPageSet() {
        return CommonUtils.calcEllipsisPages(totalPages, pageNumber);
    }

}