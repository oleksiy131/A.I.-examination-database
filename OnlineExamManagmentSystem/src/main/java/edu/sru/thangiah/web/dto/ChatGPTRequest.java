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
public class ChatGPTRequest {	//testing

	   @JsonProperty("model")
	    private String model;

	    @JsonProperty("messages")
	    private List<Message> messages;

    public ChatGPTRequest(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user",prompt));
    }

	public ChatGPTRequest(Model model2, String prompt) {
		// TODO Auto-generated constructor stub
	}
}