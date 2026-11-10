package com.maheshpai.financialhub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category; // Maps to "Articles" or "Blogs"
    private String title;
    private String author;
    private String imageUrl;
    private String slug;
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private LocalDateTime createdAt = LocalDateTime.now();

    // Generate Getters and Setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
 // ---> ADD THESE MISSING GETTERS/SETTERS <---
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setAuthor(String author) { this.author = author; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}