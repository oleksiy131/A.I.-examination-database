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
    private static final String MULTIPLE_CHOICE_FILE_PATH = "/static/chapterOne.xlsx";
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
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i += 5) {
                Row questionRow = sheet.getRow(i);
                if (questionRow == null || questionRow.getCell(0) == null) {
                    continue;
                }
                Question question = new Question();
                question.setQuestionText(getCellValue(questionRow.getCell(0)));
                Map<String, String> options = new HashMap<>();
                for (int j = 1; j <= 4; j++) {
                    Row optionRow = sheet.getRow(i + j);
                    if (optionRow == null || optionRow.getCell(0) == null || optionRow.getCell(1) == null) {
                        continue;
                    }
                    options.put(getCellValue(optionRow.getCell(0)), getCellValue(optionRow.getCell(1)));
                }
                question.setOptions(options);
                allQuestions.add(question);
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
