package edu.sru.thangiah.domain;

import java.util.List;
import java.util.Map;

public class ExamResult {
	
	private int score;
    private List<String> correctAnswers;
    private List<String> incorrectAnswers;
    private Map<String, String> incorrectAnswersWithCorrections;

	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public List<String> getCorrectAnswers() {
		return correctAnswers;
	}
	public void setCorrectAnswers(List<String> correctAnswers) {
		this.correctAnswers = correctAnswers;
	}
	public List<String> getIncorrectAnswers() {
		return incorrectAnswers;
	}
	public void setIncorrectAnswers(List<String> incorrectAnswers) {
		this.incorrectAnswers = incorrectAnswers;
	}
	
	public Map<String, String> getIncorrectAnswersWithCorrections() {
        return incorrectAnswersWithCorrections;
    }

    public void setIncorrectAnswersWithCorrections(Map<String, String> incorrectAnswersWithCorrections) {
        this.incorrectAnswersWithCorrections = incorrectAnswersWithCorrections;
    }

    
}
