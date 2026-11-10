package com.maheshpai.financialhub.controller;

import com.maheshpai.financialhub.entity.Blog;

import com.maheshpai.financialhub.entity.Event;
import com.maheshpai.financialhub.entity.Gallery;
import com.maheshpai.financialhub.repository.BlogRepository;
import com.maheshpai.financialhub.repository.EventRepository;
import com.maheshpai.financialhub.repository.GalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/api")
public class AdminApiController {

    @Autowired
    private BlogRepository blogRepo;
    
    @Autowired
    private EventRepository eventRepo;
    
    @Autowired
    private GalleryRepository galleryRepo;

    // NEW: Save directly to a root folder named "uploads" instead of the static folder
    private static final String UPLOAD_DIR = "uploads/";

    // ==========================================
    // BLOGS / INSIGHTS API
    // ==========================================
    @GetMapping("/blogs")
    public List<Blog> getAllBlogs() {
        return blogRepo.findAll();
    }

    @PostMapping("/blogs")
    public ResponseEntity<?> createBlog(@RequestBody Blog blog) {
        try {
            // Generate a safe slug
            if (blog.getTitle() != null) {
                String generatedSlug = blog.getTitle().toLowerCase().replaceAll("[^a-z0-9]+", "-");
                generatedSlug += "-" + (System.currentTimeMillis() % 1000);
                blog.setSlug(generatedSlug);
            }
            
            Blog savedBlog = blogRepo.save(blog);
            return ResponseEntity.ok(savedBlog);
        } catch (Exception e) {
            e.printStackTrace(); // This will print the exact error in your Eclipse Console
            return ResponseEntity.badRequest().body("Error saving blog: " + e.getMessage());
        }
    }

    @DeleteMapping("/blogs/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable Long id) {
        blogRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // EVENTS API
    // ==========================================
    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        Event savedEvent = eventRepo.save(event);
        return ResponseEntity.ok(savedEvent);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // GALLERY API (Handles File Uploads & Videos)
    // ==========================================
    @GetMapping("/gallery")
    public List<Gallery> getAllGalleryItems() {
        return galleryRepo.findAll();
    }


    @PostMapping("/gallery/upload")
    public ResponseEntity<?> uploadGalleryItem(
            @RequestParam("category") String category,
            @RequestParam("title") String title,
            @RequestParam("mediaType") String mediaType,
            @RequestParam(value = "videoUrl", required = false) String videoUrl,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            Gallery gallery = new Gallery();
            gallery.setCategory(category);
            gallery.setTitle(title);
            gallery.setMediaType(mediaType);

            if ("VIDEO".equalsIgnoreCase(mediaType)) {
                gallery.setVideoUrl(videoUrl);
            } 
            // FIX: Ensure file isn't empty and has an actual size before trying to save it
            else if (file != null && !file.isEmpty() && file.getSize() > 0) {
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String originalFileName = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");
                String fileName = UUID.randomUUID().toString().substring(0, 8) + "_" + originalFileName;
                
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, file.getBytes());

                gallery.setFileUrl("/uploads/" + fileName);
            }

            Gallery savedItem = galleryRepo.save(gallery);
            return ResponseEntity.ok(savedItem);

        } catch (Exception e) {
            // FIX: Catch all exceptions, not just IOExceptions
            e.printStackTrace(); 
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/gallery/{id}")
    public ResponseEntity<?> deleteGalleryItem(@PathVariable Long id) {
        galleryRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}