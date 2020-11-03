package ua.training.mytestingapp.util;

import java.util.List;

public class PageInfo<T> {

    private final List<T> content;
    private final int number;
    private final int totalPages;

    public PageInfo(List<T> content, int number, int totalPages) {
        this.content = content;
        this.number = number;
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public int getNumber() {
        return number;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
