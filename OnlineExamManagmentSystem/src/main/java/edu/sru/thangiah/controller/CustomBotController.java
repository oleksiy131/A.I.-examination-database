package edu.sru.thangiah.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import edu.sru.thangiah.web.dto.ChatGPTRequest;
import edu.sru.thangiah.web.dto.ChatGptResponse;


@RestController
@RequestMapping("/bot")
public class CustomBotController {
	
    private List<Map<String, Object>> quizData;


    @Value("${openai.model}")
    private String model;

    @Value(("${openai.api.url}"))
    private String apiURL;

    @Autowired
    private RestTemplate template;
    
    @GetMapping("/chat")
    public String chat(@RequestParam("prompt") String prompt){
        ChatGPTRequest request=new ChatGPTRequest(model, prompt);
        ChatGptResponse chatGptResponse = template.postForObject(apiURL, request, ChatGptResponse.class);
        return chatGptResponse.getChoices().get(0).getMessage().getContent();
    }
    
    @GetMapping("/quiz")
    public List<Map<String, Object>> getQuiz() {
        quizData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            quizData.add(generateQuestion());
        }
        return quizData;
    }
    
    private Map<String, Object> generateQuestion() {
        String prompt = "Generate a math multiple choice question.";
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);
        ChatGptResponse chatGptResponse = template.postForObject(apiURL, request, ChatGptResponse.class);
        String content = chatGptResponse.getChoices().get(0).getMessage().getContent();
        
        // This is a simplified example, you'll need to properly extract question and answer choices from content
        Map<String, Object> questionMap = new HashMap<>();
        questionMap.put("question", content);  // Modify this line based on the actual format of the content
        // ... extract and add answer choices to the questionMap
        
        return questionMap;
    }
  
}