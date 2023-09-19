package edu.sru.thangiah.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String recipientEmail, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(recipientEmail);
        mail.setSubject(subject);
        mail.setText(message);

        javaMailSender.send(mail);
    }
}