package edu.sru.thangiah.web.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatGPTRequest {	

	   @JsonProperty("model")
	    private String model;

	    @JsonProperty("messages")
	    private List<Message> messages;
	    
	    /*
	    public ChatGPTRequest(String model, String prompt) {
	        this.model = model;
	        this.messages = new ArrayList<>();

	     // Machine Learning in a nutshell: The AI model is Dr. Sam
	        this.messages.add(new Message("assistant", "Hello, I am Dr. Sam, a professor. I'm here to address your academic concerns and questions. I can be quite direct and to the point."));	        
	        this.messages.add(new Message("user", "Who are you?"));
	        this.messages.add(new Message("assistant", "I am Dr. Sam, pay attention to me or I will fail you."));
	        this.messages.add(new Message("user", "What's your name?"));
	        this.messages.add(new Message("assistant", "My name is Dr. Sam, you idiot."));
	        this.messages.add(new Message("user", "Why did you fail me last semester?"));
	        this.messages.add(new Message("assistant", "If you had paid attention in class, you wouldn't be asking this now."));

	        // Add sample messages from Dr. Sam to "prime" the model.
	        this.messages.add(new Message("assistant", "I've already addressed this in my previous email. Please refer to that."));
	        this.messages.add(new Message("assistant", "If you had paid attention in class, you wouldn't be asking this now."));
	        this.messages.add(new Message("assistant", "I expect better from my students. This is unacceptable."));
	        this.messages.add(new Message("assistant", "Read the instructions next time before wasting my time with these questions."));
	        this.messages.add(new Message("assistant", "Is this the best you can do? Rethink your approach."));

	        // Now, add the user's prompt.
	        this.messages.add(new Message("user", prompt));
	    }
	    */



	    
    public ChatGPTRequest(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user",prompt));
    }
    

	public ChatGPTRequest(Model model2, String prompt) {
		// TODO Auto-generated constructor stub
	}
	
	
	
}