// ==========================================================================
// assets/js/main.js
// ==========================================================================

// Listen for the custom event from common.js instead of DOMContentLoaded
document.addEventListener("componentsLoaded", () => {
    
    // Shared theme handling: /assets/js/theme.js.

    // --- 2. Scroll Reveal Animations (Fade Up) ---
    const revealElements = document.querySelectorAll('.reveal, .reveal-footer');
    
    const revealOptions = {
        threshold: 0.1,
        rootMargin: "0px 0px -50px 0px"
    };

    const revealOnScroll = new IntersectionObserver(function(entries, observer) {
        entries.forEach(entry => {
            if (!entry.isIntersecting) return;
            entry.target.classList.add('active');
            observer.unobserve(entry.target); // Run once
        });
    }, revealOptions);

    revealElements.forEach(el => revealOnScroll.observe(el));

    // --- 3. Cursor Glow Effect (Dark Mode Only) ---
    let cursorGlow = document.getElementById('cursor-glow');
    if (!cursorGlow) {
        cursorGlow = document.createElement('div');
        cursorGlow.id = 'cursor-glow';
        document.body.appendChild(cursorGlow);
    }

    document.addEventListener('mousemove', (e) => {
        if (document.documentElement.getAttribute('data-theme') === 'dark') {
            cursorGlow.style.opacity = '1';
            cursorGlow.style.left = e.clientX + 'px';
            cursorGlow.style.top = e.clientY + 'px';
        } else {
            cursorGlow.style.opacity = '0';
        }
    });

    document.addEventListener('mouseleave', () => {
        if(cursorGlow) cursorGlow.style.opacity = '0';
    });

    // --- 4. Floating Scroll to Top Logic ---
    const scrollTopBtn = document.getElementById('scrollTopBtn');
    
    if (scrollTopBtn) {
        window.addEventListener('scroll', () => {
            if (window.scrollY > 400) {
                scrollTopBtn.classList.add('visible');
            } else {
                scrollTopBtn.classList.remove('visible');
            }
        });

        scrollTopBtn.addEventListener('click', () => {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
    }
});