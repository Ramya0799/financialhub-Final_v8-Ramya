package com.maheshpai.financialhub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Serves files uploaded by AdminApiController (stored under ./uploads) at
 * the public URL prefix /uploads/** so Gallery.imageUrl values like
 * "/uploads/abc_photo.jpg" resolve correctly.
 *
 * Uploads are kept OUTSIDE the packaged jar/static folder so they survive
 * rebuilds and redeploys.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** Absolute path of the upload directory. Defaults to ./uploads next to
     *  the working directory, overridable via UPLOAD_DIR env var or
     *  --upload.dir JVM arg. */
    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
