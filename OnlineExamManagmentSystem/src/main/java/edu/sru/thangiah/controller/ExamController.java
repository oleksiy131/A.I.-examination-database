package edu.sru.thangiah.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.sru.thangiah.domain.Question;
import edu.sru.thangiah.service.ExamService;

import java.util.List;

@Controller
public class ExamController {
    @Autowired
    private ExamService examService;

    @GetMapping("/generateExam")
    public String generateExam(Model model) {
        List<Question> questions = examService.getRandomQuestions();
        model.addAttribute("questions", questions);
        return "exam";
    }
}
