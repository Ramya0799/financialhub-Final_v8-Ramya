const API_BASE_URL = 'http://localhost:8080/api/v1';
/**
 * Fetches all blogs and renders them into a specified container.
 */
async function loadBlogs(containerId) {
    try {
        const response = await fetch(`${API_BASE_URL}/blogs`);
        if (!response.ok) throw new Error('Network response was not ok');
        
        const blogs = await response.json();
        const container = document.getElementById(containerId);
        
        if (!container) return;
        
        container.innerHTML = blogs.map(blog => `
            <div class="col-md-4 mb-4">
                <div class="card glass-card h-100">
                    <div class="card-body">
                        <h5 class="card-title">${blog.title}</h5>
                        <h6 class="card-subtitle mb-2 text-muted">By ${blog.author}</h6>
                        <p class="card-text">${blog.content.substring(0, 100)}...</p>
                        <a href="blog-detail.html?id=${blog.id}" class="btn btn-outline-primary btn-sm">Read More</a>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error fetching blogs:', error);
    }
}

// Event Listeners for DOM Load
document.addEventListener('DOMContentLoaded', () => {
    // If on the blog page, load the blogs
    if (document.getElementById('blog-container')) {
        loadBlogs('blog-container');
    }
});