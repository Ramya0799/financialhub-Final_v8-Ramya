package com.maheshpai.financialhub.service;

import com.maheshpai.financialhub.entity.Blog;
import com.maheshpai.financialhub.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public Blog getBlogById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
    }

    public Blog createBlog(Blog blog) {
        // Here you could add logic to auto-generate the slug if it's empty
        return blogRepository.save(blog);
    }

    public Blog updateBlog(Long id, Blog updatedBlog) {
        Blog existingBlog = getBlogById(id);
        existingBlog.setTitle(updatedBlog.getTitle());
        existingBlog.setSlug(updatedBlog.getSlug());
        existingBlog.setContent(updatedBlog.getContent());
        existingBlog.setAuthor(updatedBlog.getAuthor());
        existingBlog.setImageUrl(updatedBlog.getImageUrl());
        return blogRepository.save(existingBlog);
    }

    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }
}