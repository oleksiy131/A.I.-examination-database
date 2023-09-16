package edu.sru.thangiah.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailVerificationService {
	@Autowired
	private JavaMailSender VerificationSender;
	
	public void sendVerifyEmail(String toEmail, String subject,String body) {
		
		SimpleMailMessage message= new SimpleMailMessage();
		message.setFrom("oemsexamsystem@gmail.com");
		message.setTo(toEmail);
		message.setText(body);
		message.setSubject(subject);
		
		VerificationSender.send(message);

		
	}
	
	
	
}
