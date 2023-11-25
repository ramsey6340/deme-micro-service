package com.infinity.servicemail.service;

import com.infinity.servicemail.exceptions.InternalServerException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public ResponseEntity<String> sendEmail(String to, String subject, String text) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            message.setFrom(new InternetAddress("eniac5project@gmail.com"));
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(subject);

            String htmlContent = "<h1 style=\"text-align: center\">Email vérification</h1>" +
                    "<p style=\"text-align: center\">Votre code de vérification d'e-mail est :</p>"+
                    "<h2 style=\"text-align: center\">"+text+"</h2>";
            message.setContent(htmlContent, "text/html; charset=utf-8");

            javaMailSender.send(message);
            return ResponseEntity.ok("Email envoyé avec succès");
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }
}
