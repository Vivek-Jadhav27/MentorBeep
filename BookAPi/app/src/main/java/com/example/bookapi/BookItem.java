package com.example.bookapi;

public class BookItem {

    String title , publish , imageURL;

    public BookItem(){}
    public BookItem(String title, String publish) {
        this.title = title;
        this.publish = publish;
    }

    public BookItem(String title, String publish, String imageURL) {
        this.title = title;
        this.publish = publish;
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }
}
