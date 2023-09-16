package edu.sru.thangiah.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Service;

//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.service.EmailVerificationService;

@Controller
public class EmailVerifyController {
	
	

    @Autowired
    private static EmailVerificationService verifySender;
    
    //@EventListener(ApplicationReadyEvent.class)
	public static void sendMail() {
    	//User user
    	
    	//String name = user.getUsername();
        //String email = user.getEmail();
		
		String name = "Brent";
		String email = "brent.bkosior@gmail.com";
		
        String EmailContent = name+"this is a test and I really want it to work";
        
    	System.out.println();
    	verifySender.sendVerifyEmail(email,"Verification",EmailContent);
	}

}
