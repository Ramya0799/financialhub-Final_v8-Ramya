/* Shared theme controller. Load in <head> before styles to prevent a light flash. */
(() => {
    'use strict';
    const root = document.documentElement;
    const normalize = value => value === 'dark' ? 'dark' : 'light';
    let saved = 'light';
    try { saved = localStorage.getItem('theme'); } catch (_) { /* Private storage may be unavailable. */ }
    function apply(value) {
        const theme = normalize(value);
        root.dataset.theme = theme;
        root.style.colorScheme = theme;
        const button = document.getElementById('site-theme-toggle');
        if (button) {
            const dark = theme === 'dark';
            button.setAttribute('aria-pressed', String(dark));
            button.title = dark ? 'Switch to light theme' : 'Switch to dark theme';
            button.querySelector('.theme-icon').textContent = dark ? '☀' : '☾';
            button.querySelector('.theme-label').textContent = dark ? 'Light theme' : 'Dark theme';
        }
    }
    apply(saved);
    document.addEventListener('DOMContentLoaded', () => {
        const button = document.createElement('button');
        button.id = 'site-theme-toggle';
        button.type = 'button';
        button.setAttribute('aria-label', 'Dark theme');
        button.innerHTML = '<span class="theme-icon" aria-hidden="true"></span><span class="theme-label" aria-hidden="true"></span>';
        button.addEventListener('click', () => {
            const next = root.dataset.theme === 'dark' ? 'light' : 'dark';
            apply(next);
            try { localStorage.setItem('theme', next); } catch (_) { /* Toggle still works for this page. */ }
        });
        document.body.appendChild(button);
        apply(root.dataset.theme);
    });
    window.addEventListener('storage', event => {
        if (event.key === 'theme' || event.key === null) apply(event.newValue);
    });
})();
