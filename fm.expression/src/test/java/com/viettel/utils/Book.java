package com.viettel.utils;

public class Book {

    String id;
    String title;

    public Book(String id, String title) {
        this.id = id;
        this.title = title;
    }

    @SuppressWarnings("unused")
    private String formatBook() {
        return id + " > " + title;
    }
}
