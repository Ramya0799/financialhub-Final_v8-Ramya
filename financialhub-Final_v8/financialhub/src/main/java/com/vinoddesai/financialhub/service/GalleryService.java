package com.maheshpai.financialhub.service;

import com.maheshpai.financialhub.entity.Gallery;
import com.maheshpai.financialhub.repository.GalleryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final Path fileStorageLocation;

    public GalleryService(GalleryRepository galleryRepository, @Value("${file.upload-dir}") String uploadDir) {
        this.galleryRepository = galleryRepository;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            // Create the directory if it doesn't exist
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Gallery uploadMedia(MultipartFile file, String title, String mediaType) {
        // Normalize file name and add UUID to prevent overwriting files with the same name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Generate the URL that the frontend will use to display the image
            String fileDownloadUri = "/uploads/gallery/" + newFileName;

            // Save to database
            Gallery gallery = new Gallery();
            gallery.setTitle(title);
            gallery.setMediaType(mediaType);
            gallery.setFileUrl(fileDownloadUri);
            
            return galleryRepository.save(gallery);

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public List<Gallery> getAllMedia() {
        return galleryRepository.findAll();
    }

    public void deleteMedia(Long id) {
        Gallery gallery = galleryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        // Delete the physical file from the server
        try {
            String fileName = gallery.getFileUrl().substring(gallery.getFileUrl().lastIndexOf("/") + 1);
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            System.err.println("Could not delete physical file: " + ex.getMessage());
        }

        // Delete from database
        galleryRepository.deleteById(id);
    }
}