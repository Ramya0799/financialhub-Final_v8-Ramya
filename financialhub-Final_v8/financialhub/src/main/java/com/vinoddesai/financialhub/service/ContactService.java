package com.maheshpai.financialhub.service;

import com.maheshpai.financialhub.entity.ContactMessage;
import com.maheshpai.financialhub.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final JavaMailSender mailSender;

    @Value("${admin.notification.email}")
    private String adminEmail;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public ContactService(ContactRepository contactRepository, JavaMailSender mailSender) {
        this.contactRepository = contactRepository;
        this.mailSender = mailSender;
    }

    public ContactMessage submitMessage(ContactMessage message) {
        ContactMessage savedMessage = contactRepository.save(message);
        sendNotificationEmail(savedMessage);
        return savedMessage;
    }

    public List<ContactMessage> getAllMessages() {
        return contactRepository.findAll();
    }

    public ContactMessage markAsRead(Long id) {
        ContactMessage message = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        return contactRepository.save(message);
    }

    public void deleteMessage(Long id) {
        contactRepository.deleteById(id);
    }

    private void sendNotificationEmail(ContactMessage message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromAddress);
            mailMessage.setTo(adminEmail);
            if (message.getEmail() != null && !message.getEmail().isBlank()) {
                mailMessage.setReplyTo(message.getEmail());
            }
            mailMessage.setSubject("New Website Inquiry: " + message.getSubject());
            mailMessage.setText(
                "You have received a new inquiry from Vinod Desai Financial Hub.\n\n" +
                "Name: " + message.getName() + "\n" +
                "Email: " + message.getEmail() + "\n" +
                "Phone: " + message.getPhone() + "\n" +
                "Message: \n" + message.getMessage()
            );
            mailSender.send(mailMessage);
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
            e.printStackTrace();
            // We catch the error so the user's message still gets saved to the database
            // even if the email server has a temporary issue.
        }
    }
}
