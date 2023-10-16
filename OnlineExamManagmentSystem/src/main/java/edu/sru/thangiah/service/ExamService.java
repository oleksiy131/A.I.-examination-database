package edu.sru.thangiah.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.domain.ExamResult;
import edu.sru.thangiah.domain.Question;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamService {
	
    private ExamResult storedExamResult;

    private List<Question> allQuestions = new ArrayList<>();
    

    public ExamService() {
        readAllQuestions();
    }
    
    public List<Question> getAllQuestions() {
        return allQuestions;
    }
    
    public ExamResult evaluateAnswers(Map<Integer, String> userAnswers) {
        int score = 0;
        Map<String, String> correctAnswers = new HashMap<>();
        Map<String, String> incorrectAnswersWithCorrections = new HashMap<>();

        for (Map.Entry<Integer, String> entry : userAnswers.entrySet()) {
            Integer questionIndex = entry.getKey();
            String userAnswer = entry.getValue();

            if (questionIndex < 0 || questionIndex >= allQuestions.size()) {
                continue;
            }

            Question question = allQuestions.get(questionIndex);
            if (question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
                score++;
                correctAnswers.put(question.getQuestionText(), userAnswer);
            } else {
                incorrectAnswersWithCorrections.put(question.getQuestionText(), question.getCorrectAnswer());
            }
        }

        ExamResult result = new ExamResult();
        result.setScore(score);
        result.setCorrectAnswers(new ArrayList<>(correctAnswers.keySet())); // or store the entire map if needed
        result.setIncorrectAnswersWithCorrections(incorrectAnswersWithCorrections);

        return result;
    }


    public void storeExamResultForUser(ExamResult result) {
        // In a real-world application, you might store it in a database or session. Here we're using a simple in-memory storage.
        this.storedExamResult = result;
    }

    public ExamResult getStoredExamResultForUser() {
        // In a real-world application, you'd retrieve this from a database or session. Here we're using simple in-memory storage.
        return this.storedExamResult;
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

    private List<Question> readTrueFalseQuestions(String resourcePath) {
        List<Question> questions = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream(resourcePath);
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
                String answer = getCellValue(row.getCell(1));
                if (answer.startsWith("Ans: ")) {
                    question.setCorrectAnswer(answer.substring(5).trim());
                }
                questions.add(question);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }


    private void readAllQuestions() {
        readMultipleChoiceQuestions(); // This reads all the multiple-choice questions as before

        
        for (int chapter = 1; chapter <= 4; chapter++) { // Assuming there are 4 chapters
            String tfQuestionsFilePath = "/static/chapter-" + chapter + "_TF.xlsx"; // Construct the file path
            allQuestions.addAll(readTrueFalseQuestions(tfQuestionsFilePath)); // Read and add to the main list
        }
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
        // Based on the chapter, we decide which files to read.
        if (chapterOrExamType <= 4) {
            // For individual chapters, we load specific files.
            examQuestions.addAll(readQuestionsFromExcel("/static/chapter-" + chapterOrExamType + ".xlsx", true)); // true indicates we want to read true/false questions as well.
        } else if (chapterOrExamType == 5) { // Mid-Term
            examQuestions.addAll(readQuestionsFromExcel("/static/chapter-1.xlsx", false));
            examQuestions.addAll(readQuestionsFromExcel("/static/chapter-2.xlsx", false));
        } else if (chapterOrExamType == 6) { // Final Exam
            for (int i = 1; i <= 4; i++) {
                examQuestions.addAll(readQuestionsFromExcel("/static/chapter-" + i + ".xlsx", false));
            }
        }
        Collections.shuffle(examQuestions);
        return examQuestions.stream().limit(numberOfQuestions).collect(Collectors.toList());
    }


    private List<Question> readQuestionsFromExcel(String resourcePath, boolean includeTrueFalse) {
        List<Question> questions = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream(resourcePath);
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
                questions.add(question);
                i++;
            }
            // If we need to include true/false questions, we read them from a designated file.
            if (includeTrueFalse) {
                questions.addAll(readTrueFalseQuestions(resourcePath.replace(".xlsx", "_TF.xlsx"))); // Assuming a naming convention for true/false files.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }
    

    
    public byte[] createExcelFile(List<Question> questions) {
        // This method calls the ExcelFileExporter utility class to create the Excel content.
        ByteArrayInputStream in = ExcelGeneratorService.createExcelFile(questions);
        try {
            return IOUtils.toByteArray(in); // Convert the InputStream to a byte array
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert input stream to byte array", e);
        }
    }

    private void createCell(Row row, int column, String value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
    }
}
    
    

