package edu.sru.thangiah.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import edu.sru.thangiah.service.ExcelGeneratorService;
import edu.sru.thangiah.web.dto.ChatGPTRequest;
import edu.sru.thangiah.web.dto.ChatGptResponse;


@RestController
@RequestMapping("/bot")
public class CustomBotController {
	
	@Autowired
    private ExcelGeneratorService excelGeneratorService;
	
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
    public ModelAndView getQuiz() {
        quizData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            quizData.add(generateQuestion());
        }
        
        ModelAndView modelAndView = new ModelAndView("quiz");
        modelAndView.addObject("quizData", quizData);
        return modelAndView;
    }
    
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadQuiz() throws IOException {
        byte[] excelData = excelGeneratorService.generateExcel(quizData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "quiz.xlsx");

        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }
    
    
    
    private Map<String, Object> generateQuestion() {
        String prompt = "Generate a math multiple choice question.";
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);
        ChatGptResponse chatGptResponse = template.postForObject(apiURL, request, ChatGptResponse.class);
        String content = chatGptResponse.getChoices().get(0).getMessage().getContent();
        
        // Assume content is formatted as "Question text\nA) Choice 1\nB) Choice 2\nC) Choice 3\nD) Choice 4"
        String[] lines = content.split("\n");
        List<String> choices = Arrays.asList(Arrays.copyOfRange(lines, 1, lines.length));
        
        Map<String, Object> questionMap = new HashMap<>();
        questionMap.put("question", lines[0]);
        questionMap.put("choices", choices);
        
        return questionMap;
    }

  
}