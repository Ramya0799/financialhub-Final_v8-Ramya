package com.maheshpai.financialhub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String title;
    private String eventDate;
    private String imageUrl;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    // ---> ADD THESE MISSING FIELDS <---
    private String location;
    private boolean upcoming;

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // ---> ADD THESE MISSING GETTERS/SETTERS <---
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public boolean isUpcoming() { return upcoming; }
    public void setUpcoming(boolean upcoming) { this.upcoming = upcoming; }
}