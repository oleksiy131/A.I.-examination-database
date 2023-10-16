package edu.sru.thangiah.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.domain.Question;

import java.io.InputStream;
import java.util.*;

/*
 *____  __    __        _ _ 
 / __ \/ /__ / /__ ___ (_|_)
/ /_/ / / -_)  '_/(_-</ / / 
\____/_/\__/_/\_\/___/_/_/  
                        
 */



@Service
public class ExamService {
	
    // Constants for file paths
    private static final String MULTIPLE_CHOICE_FILE_PATH = "/static/chapter-4.xlsx";
    private static final String TRUE_FALSE_FILE_PATH = "/static/chapterOneTF.xlsx";

    // List to hold all questions
    private List<Question> allQuestions = new ArrayList<>();

    // Constructor to read all questions into memory
    public ExamService() {
        readAllQuestions();
    }

    // Method to get a random set of questions
    public List<Question> getRandomQuestions() {
        List<Question> selectedQuestions = new ArrayList<>();
        Collections.shuffle(allQuestions);
        int toIndex = Math.min(10, allQuestions.size());
        selectedQuestions.addAll(allQuestions.subList(0, toIndex));
        return selectedQuestions;
    }

 // Method to read multiple-choice questions from Excel
    private void readMultipleChoiceQuestions() {
        try (InputStream is = getClass().getResourceAsStream(MULTIPLE_CHOICE_FILE_PATH);
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); ) {
                Row questionRow = sheet.getRow(i);
                if (questionRow == null || questionRow.getCell(0) == null) {
                    continue;
                }
                Question question = new Question();
                question.setQuestionText(getCellValue(questionRow.getCell(0)));
                Map<String, String> options = new HashMap<>();
                // Assuming there are always 4 options
                for (int j = 1; j <= 4; j++) {
                    Row optionRow = sheet.getRow(i + j);
                    if (optionRow == null || optionRow.getCell(0) == null || optionRow.getCell(1) == null) {
                        continue;
                    }
                    options.put(getCellValue(optionRow.getCell(0)), getCellValue(optionRow.getCell(1)));
                }
                question.setOptions(options);

                // New code to read the answer, which is located after the options
                i += 5; // Move to the answer row
                Row answerRow = sheet.getRow(i);
                if (answerRow != null && answerRow.getCell(0) != null) {
                    String answer = getCellValue(answerRow.getCell(0));
                    // Extract the actual answer from the "Ans: " prefix
                    if (answer.startsWith("Ans: ")) {
                        question.setCorrectAnswer(answer.substring(5).trim()); // Set the answer, excluding the "Ans: " part
                    }
                }

                allQuestions.add(question);
                i++; // Move to the next question block
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Method to read true/false questions from Excel
    private void readTrueFalseQuestions() {
        try (InputStream is = getClass().getResourceAsStream(TRUE_FALSE_FILE_PATH);
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null) {
                    continue;
                }
                Question question = new Question();
                question.setQuestionText(getCellValue(row.getCell(0)));
                Map<String, String> options = new HashMap<>();
                options.put("A", "True");
                options.put("B", "False");
                question.setOptions(options);
                allQuestions.add(question);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to read all questions (both multiple-choice and true/false)
    private void readAllQuestions() {
        readMultipleChoiceQuestions();
        readTrueFalseQuestions();
    }

    // Utility method to get cell value as a string
    private String getCellValue(Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return "";
    }
}
