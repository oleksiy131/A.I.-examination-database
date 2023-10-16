package edu.sru.thangiah.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import edu.sru.thangiah.domain.Question;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private List<Question> allQuestions = new ArrayList<>();

    public ExamService() {
        readAllQuestions();
    }

    public List<Question> getRandomQuestions() {
        List<Question> selectedQuestions = new ArrayList<>();
        Collections.shuffle(allQuestions);
        int toIndex = Math.min(10, allQuestions.size());
        selectedQuestions.addAll(allQuestions.subList(0, toIndex));
        return selectedQuestions;
    }

    private void readMultipleChoiceQuestions() {
        try (InputStream is = getClass().getResourceAsStream("/static/chapter-4.xlsx");
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
                for (int j = 1; j <= 4; j++) {
                    Row optionRow = sheet.getRow(i + j);
                    if (optionRow == null || optionRow.getCell(0) == null || optionRow.getCell(1) == null) {
                        continue;
                    }
                    options.put(getCellValue(optionRow.getCell(0)), getCellValue(optionRow.getCell(1)));
                }
                question.setOptions(options);
                i += 5;
                Row answerRow = sheet.getRow(i);
                if (answerRow != null && answerRow.getCell(0) != null) {
                    String answer = getCellValue(answerRow.getCell(0));
                    if (answer.startsWith("Ans: ")) {
                        question.setCorrectAnswer(answer.substring(5).trim());
                    }
                }
                allQuestions.add(question);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readTrueFalseQuestions() {
        try (InputStream is = getClass().getResourceAsStream("/static/chapterOneTF.xlsx");
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

    private void readAllQuestions() {
        readMultipleChoiceQuestions();
        readTrueFalseQuestions();
    }

    private String getCellValue(Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return "";
    }

    public List<Question> generateExam(int chapterOrExamType, int numberOfQuestions) {
        List<Question> examQuestions = new ArrayList<>();

        // Function to read questions from specific files and add them to the examQuestions list
        java.util.function.Consumer<String> readQuestions = (String filePath) -> {
            try (InputStream is = getClass().getResourceAsStream(filePath)) {
                if (is != null) {
                    examQuestions.addAll(readQuestionsFromExcel(is));
                }
            } catch (IOException e) {
                e.printStackTrace(); // exception handling
            }
        };

        if (chapterOrExamType <= 4) {
            // For chapter exams, read both multiple-choice and true/false questions
            readQuestions.accept("/static/chapter-" + chapterOrExamType + ".xlsx");
            readQuestions.accept("/static/chapter-" + chapterOrExamType + "-tf.xlsx");
        } else if (chapterOrExamType == 5) { // Mid-Term
            // For the mid-term
            readQuestions.accept("/static/chapter-1.xlsx");
            readQuestions.accept("/static/chapter-1-tf.xlsx");
            readQuestions.accept("/static/chapter-2.xlsx");
            readQuestions.accept("/static/chapter-2-tf.xlsx");
            // Add more chapters if needed
        } else if (chapterOrExamType == 6) { // Final Exam
            // For the final exam, aggregate questions from all chapters
        	
            for (int i = 1; i <= 4; i++) { // ASSUMING 4 CHAPTERS!!
                readQuestions.accept("/static/chapter-" + i + ".xlsx");
                readQuestions.accept("/static/chapter-" + i + "-tf.xlsx");
            }
        }

        // Shuffle the collected questions
        Collections.shuffle(examQuestions);

        // Limit the number of questions based on the specified count
        return examQuestions.stream()
                            .limit(numberOfQuestions)
                            .collect(Collectors.toList());
    }


    private List<Question> readQuestionsFromExcel(InputStream is) {
        List<Question> questions = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); ) {
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
                i += 5;
                Row answerRow = sheet.getRow(i);
                if (answerRow != null && answerRow.getCell(0) != null) {
                    String answer = getCellValue(answerRow.getCell(0));
                    if (answer.startsWith("Ans: ")) {
                        question.setCorrectAnswer(answer.substring(5).trim());
                    }
                }
                questions.add(question);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }
}
