
package com.maheshpai.financialhub.controller;

import com.maheshpai.financialhub.entity.ContactMessage;
import com.maheshpai.financialhub.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class PublicContactController {

    private final ContactService contactService;

    public PublicContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitMessage(@RequestBody ContactMessage message) {
        contactService.submitMessage(message);
        return ResponseEntity.ok("Message submitted successfully");
    }
}