// ==========================================================================
// assets/js/contact.js
// ==========================================================================

document.addEventListener("DOMContentLoaded", function () {
    const contactForm = document.getElementById("consultationForm");
    const formAlert = document.getElementById("form-alert");
    const alertMessage = formAlert.querySelector(".alert-message");
    const alertIcon = formAlert.querySelector(".alert-icon");
    const submitBtn = document.getElementById("submitBtn");
    const btnText = submitBtn.querySelector(".btn-text");
    const loader = submitBtn.querySelector(".loader");

	// --- Prevent Past Dates in Date Picker ---
	    const dateInput = document.getElementById("date");
	    if (dateInput) {
	        // Get today's date and format it as YYYY-MM-DD
	        const today = new Date().toISOString().split('T')[0];
	        dateInput.setAttribute('min', today);
	    }
    // --- Button Ripple Effect ---
    submitBtn.addEventListener('click', function (e) {
        let x = e.clientX - e.target.getBoundingClientRect().left;
        let y = e.clientY - e.target.getBoundingClientRect().top;
        let ripples = document.createElement('span');
        ripples.style.left = x + 'px';
        ripples.style.top = y + 'px';
        ripples.classList.add('ripple');
        this.appendChild(ripples);
        setTimeout(() => ripples.remove(), 600);
    });

    if (contactForm) {
        contactForm.addEventListener("submit", function (event) {
            event.preventDefault(); 

            // UI Loading State
            submitBtn.disabled = true;
            btnText.style.display = "none";
            loader.style.display = "block";
            formAlert.style.display = "none";

            // Gather all form data
            const name = document.getElementById("name").value.trim();
            const email = document.getElementById("email").value.trim();
            const phone = document.getElementById("phone").value.trim();
            
            // Advanced Fields
            const service = document.getElementById("service").value;
            const mode = document.querySelector('input[name="mode"]:checked').value;
            const date = document.getElementById("date").value;
            const time = document.getElementById("time").value;
            const clientMessage = document.getElementById("message").value.trim();

            // PACKAGING DATA FOR EXISTING SPRING BOOT BACKEND
            // We embed the advanced fields directly into the standard backend entity structure.
            const contactData = {
                name: name,
                email: email,
                phone: phone,
                subject: `Consultation Request: ${service}`,
                message: `SERVICE: ${service}\nMODE: ${mode}\nDATE: ${date}\nTIME: ${time}\n\nCLIENT MESSAGE:\n${clientMessage}`
            };

            // Call Spring Boot API
            fetch("/api/contact/submit", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(contactData)
            })
            .then(response => {
                if (!response.ok) throw new Error("Server error");
                return response.text();
            })
            .then(() => {
                formAlert.className = "premium-alert success";
                alertIcon.className = "fas fa-check-circle alert-icon";
                alertMessage.textContent = "Thank you. Our advisor will contact you shortly to confirm your slot.";
                formAlert.style.display = "flex";
                contactForm.reset();
            })
            .catch(error => {
                console.error("Error:", error);
                formAlert.className = "premium-alert error";
                alertIcon.className = "fas fa-exclamation-circle alert-icon";
                alertMessage.textContent = "Secure transmission failed. Please try again or call our direct line.";
                formAlert.style.display = "flex";
            })
            .finally(() => {
                submitBtn.disabled = false;
                btnText.style.display = "block";
                loader.style.display = "none";
            });
        });
    }
});