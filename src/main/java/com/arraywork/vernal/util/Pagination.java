package com.arraywork.vernal.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;

import lombok.Data;

/**
 * Pagination Object extends framework
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/03/10
 */
@Data
public class Pagination<T> {

    private long totalRecords;
    private int totalPages;
    private int pageNumber;
    private boolean hasPrevious;
    private boolean hasNext;
    private boolean hasContent;
    private Object content;

    public Pagination(Page<T> page) {
        setTotalRecords(page.getTotalElements());
        setTotalPages(page.getTotalPages());
        setPageNumber(page.getNumber() + 1);
        setHasPrevious(page.hasPrevious());
        setHasNext(page.hasNext());
        setHasContent(page.hasContent());
        setContent(page.getContent());
    }

    /**
     * 生成带省略号（以0表示）的页码数组
     *
     * @param totalPages   总页数
     * @param pageNumber   当前页码
     * @param aroundNumber 环绕当前页左右的页码数
     * @return 包含整数页码的数组（0表示省略号）
     */
    public static List<Integer> buildPageSet(int totalPages, int pageNumber, int aroundNumber) {
        int baseCount = aroundNumber * 2 + 5; // 总元素个数：环绕左右页码*2+当前页+省略号*2+首页+末页
        int surplus = baseCount - 2; // 只出现一个省略号时剩余元素个数
        int startIndex = 1 + 2 + aroundNumber + 1; // 前面出现省略号的临界点
        int endIndex = totalPages - 2 - aroundNumber - 1; // 后面出现省略号的临界点

        if (totalPages <= baseCount) { // 全部显示，不出现省略号
            return IntStream.rangeClosed(1, totalPages).boxed().toList();
        }
        if (pageNumber < startIndex) { // 只有后面出现省略号
            List<Integer> pages = IntStream.rangeClosed(1, surplus).boxed().collect(Collectors.toList());
            pages.add(0);
            pages.add(totalPages);
            return pages;
        }
        if (pageNumber > endIndex) { // 只有前边出现省略号
            int start = totalPages - surplus + 1;
            int end = start + surplus - 1;
            List<Integer> pages = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
            pages.add(0, 0);
            pages.add(0, 1);
            return pages;
        }
        // 两边都有省略号
        int start = pageNumber - aroundNumber;
        int end = start + aroundNumber * 2;
        List<Integer> pages = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        pages.add(0, 0);
        pages.add(0, 1);
        pages.add(0);
        pages.add(totalPages);
        return pages;
    }

    public List<Integer> getPageSet() {
        return buildPageSet(totalPages, pageNumber, 2);
    }

}