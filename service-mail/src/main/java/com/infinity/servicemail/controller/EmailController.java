package com.infinity.servicemail.controller;

import com.infinity.servicemail.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/mail/")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("send-email")
    public ResponseEntity<String> sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String text
    ) throws MessagingException {
        return emailService.sendEmail(to, subject, text);
    }
}

