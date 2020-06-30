package com.example.library.Model;

/**
 * Book class represents position in catalog.
 */
public class Book {

    private String title;
    private String author;
    private String description;
    private String photo;
    private String id;

    public Book(String title, String author, String description) {
        this.title = title;
        this.author = author;
        this.description = description;
        photo = "";
    }

    public Book() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}