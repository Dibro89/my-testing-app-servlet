package ua.training.mytestingapp.util;

import java.util.List;

public class PageInfo<T> {

    private final int page;
    private final int totalPages;
    private final List<T> data;

    public PageInfo(int page, int totalPages, List<T> data) {
        this.page = page;
        this.totalPages = totalPages;
        this.data = data;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<T> getData() {
        return data;
    }
}
