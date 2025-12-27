package com.luis.textlift_backend.features.document.domain;

public class ExtractedMetadata {
    private String author;
    private String title;
    private String subject;
    private String keywords;
    private String isbn;

    public ExtractedMetadata(String author, String title, String subject, String keywords) {
        this.author = author;
        this.title = title;
        this.subject = subject;
        this.keywords = keywords;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
