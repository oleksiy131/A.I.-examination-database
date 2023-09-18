package edu.sru.thangiah.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.model.User;

import java.util.Date;

@Service
public class EmailVerificationService {
	
		//@EventListener(ApplicationReadyEvent.class)
		public void sendMail (User user){
    	//
    	
    	String name = user.getUsername();
        String email = user.getEmail();
		
		//String name = "Brent";
		//String email = "brent.bkosior@gmail.com";
		
        System.out.println(name+"  "+email);
        
        String EmailContent = name+" this is a test and I really want it to work";
        
        //ToEmail: SubjectLine: Body:
    	sendVerifyEmail(email,"Verification",EmailContent);
	}
		
	@Autowired
	private JavaMailSender VerificationSender;
	
	public void sendVerifyEmail(String toEmail, String subject,String body) {
	

		SimpleMailMessage message= new SimpleMailMessage();
		message.setFrom("oemsexamsystem@gmail.com");
		message.setTo(toEmail);
		message.setSentDate(new Date());
		message.setReplyTo("");
		message.setText(body);
		message.setSubject(subject);
		
		System.out.println(message);
		try {
		VerificationSender.send(message);
		}
		catch(MailException e) {
		    e.printStackTrace(); // This will print the exception stack trace to the console
		}
	}
}
