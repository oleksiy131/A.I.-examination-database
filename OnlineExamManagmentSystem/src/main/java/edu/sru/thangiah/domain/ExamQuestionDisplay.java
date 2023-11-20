package edu.sru.thangiah.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionDisplay {
    private Long id;
    private String questionText;
    private String userAnswer;
    private String correctAnswerText;
}