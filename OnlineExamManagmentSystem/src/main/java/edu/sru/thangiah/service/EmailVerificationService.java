package edu.sru.thangiah.service;

import org.springframework.stereotype.Service;
import edu.sru.thangiah.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailVerificationService {
		@Autowired
		private JavaMailSender VerificationSender;
		
		//@EventListener(ApplicationReadyEvent.class)
		public void sendMail(User user){
    	//
    	
    	String name = user.getUsername();
        String email = user.getEmail();
		
		//String name = "Brent";
		//String email = "brent.bkosior@gmail.com";
		
        System.out.println(name+"  "+email);
        
        String EmailContent = name+"this is a test and I really want it to work";
        
        //ToEmail: SubjectLine: Body:
    	sendVerifyEmail(email,"Verification",EmailContent);
	}
	
	public void sendVerifyEmail(String toEmail, String subject,String body) {
	
		SimpleMailMessage message= new SimpleMailMessage();
		message.setFrom("oemsexamsystem@gmail.com");
		message.setTo(toEmail);
		message.setText(body);
		message.setSubject(subject);
		
		VerificationSender.send(message);
	}

}
