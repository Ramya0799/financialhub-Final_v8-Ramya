package com.maheshpai.financialhub.controller;

import com.maheshpai.financialhub.entity.Gallery;
import com.maheshpai.financialhub.repository.GalleryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class PublicGalleryController {
    private final GalleryRepository galleryRepository;
    public PublicGalleryController(GalleryRepository galleryRepository) {
        this.galleryRepository = galleryRepository;
    }
    @GetMapping("/api/gallery")
    public List<Gallery> getGallery() {
        return galleryRepository.findAll();
    }
}
