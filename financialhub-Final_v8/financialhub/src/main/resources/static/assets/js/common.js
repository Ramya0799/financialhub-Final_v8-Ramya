// ==========================================================================
// assets/js/common.js
// ==========================================================================

async function loadComponent(id, file) {
    const element = document.getElementById(id);
    if (!element) return; // Skip if the placeholder div isn't on the current page

    try {
        const response = await fetch(file);
        if (!response.ok) throw new Error(`Could not load ${file}`);
        const html = await response.text();
        element.innerHTML = html;
    } catch (error) {
        console.error("Component Loading Error:", error);
    }
}

document.addEventListener("DOMContentLoaded", async () => {
    // Wait for ALL components to fetch and inject into the DOM simultaneously
    await Promise.all([
        loadComponent("header", "/components/header.html"),
        loadComponent("navbar", "/components/navbar.html"),
        loadComponent("footer", "/components/footer.html")
    ]);

    // Now that the HTML is actually on the page, tell main.js it can safely run
    document.dispatchEvent(new Event("componentsLoaded"));
});