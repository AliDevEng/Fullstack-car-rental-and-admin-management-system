package com.nextcar.carrental.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PagedResponseDTO<T> {

    private List<T> items;
    private long totalCount;
    private int page;
    private int pageSize;
    private int totalPages;
    private boolean hasPreviousPage;
    private boolean hasNextPage;

    public PagedResponseDTO(Page<T> springPage) {
        this.items = springPage.getContent();
        this.totalCount = springPage.getTotalElements();
        this.page = springPage.getNumber() + 1; // Spring is 0-based, frontend expects 1-based
        this.pageSize = springPage.getSize();
        this.totalPages = springPage.getTotalPages();
        this.hasPreviousPage = springPage.hasPrevious();
        this.hasNextPage = springPage.hasNext();
    }

    public List<T> getItems() { return items; }
    public long getTotalCount() { return totalCount; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public int getTotalPages() { return totalPages; }
    public boolean isHasPreviousPage() { return hasPreviousPage; }
    public boolean isHasNextPage() { return hasNextPage; }
}
