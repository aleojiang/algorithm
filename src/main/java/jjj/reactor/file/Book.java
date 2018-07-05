package jjj.reactor.file;

import java.time.ZonedDateTime;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  14:12 05/04/2018.
 */
public class Book {
    private String title;
    private String isdn;
    private Author author;
    private String publishDate;
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getIsdn() {
        return isdn;
    }
    
    public void setIsdn(String isdn) {
        this.isdn = isdn;
    }
    
    public Author getAuthor() {
        return author;
    }
    
    public void setAuthor(Author author) {
        this.author = author;
    }
    
    public String getPublishDate() {
        return publishDate;
    }
    
    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", isdn='" + isdn + '\'' +
                ", publishDate='" + publishDate + '\'' +
                '}';
    }
}
