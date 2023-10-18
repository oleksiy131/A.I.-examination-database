
package edu.sru.thangiah.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.domain.Question;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
/*
 *____  __    __        _ _ 
 / __ \/ /__ / /__ ___ (_|_)
/ /_/ / / -_)  '_/(_-</ / / 
\____/_/\__/_/\_\/___/_/_/  
                        
 */

@Service
public class ExcelGeneratorService {

    public byte[] generateExcel(List<Map<String, Object>> quizData) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Quiz");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Question Number");
        headerRow.createCell(1).setCellValue("Question");
        headerRow.createCell(2).setCellValue("Choice");

        int rowIndex = 1;  // initialize rowIndex to 1 to start from the second row

        // Create data rows. N^2 Complexity. Might work on a quicker solution
        for (int i = 0; i < quizData.size(); i++) {
            Map<String, Object> question = quizData.get(i);

            @SuppressWarnings("unchecked")
            List<String> choices = (List<String>) question.get("choices");

            for (int j = 0; j < choices.size(); j++) {
                Row row = sheet.createRow(rowIndex++);  // Increment rowIndex for each choice to create a new row

                // Only write the question number and question text for the first choice of each question
                if (j == 0) {
                    row.createCell(0).setCellValue(i + 1);  // Question Number
                    row.createCell(1).setCellValue((String) question.get("question"));  // Question
                }

                row.createCell(2).setCellValue(choices.get(j));  // Choice
            }
        }

        // Write workbook to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        return byteArrayOutputStream.toByteArray();
    }
    

    public static ByteArrayInputStream createExcelFile(List<Question> questions) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet("Questions");

            int rowNum = 0; // Keeps track of the next row to write to

            // Style for cells
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            // Iterate through all questions
            for (Question question : questions) {
                // Write the question
                Row questionRow = sheet.createRow(rowNum++);
                Cell questionCell = questionRow.createCell(0);
                questionCell.setCellValue(question.getQuestionText());
                questionCell.setCellStyle(style);

                // Write the options below the question
                Map<String, String> options = question.getOptions(); // Assuming getOptions returns Map<String, String>
                for (Map.Entry<String, String> entry : options.entrySet()) {
                    Row optionRow = sheet.createRow(rowNum++);
                    Cell optionCell = optionRow.createCell(0);
                    optionCell.setCellValue(entry.getKey() + ". " + entry.getValue()); // e.g., "A. Option text"
                    optionCell.setCellStyle(style);
                }

                // Write the correct answer after the options
                Row answerRow = sheet.createRow(rowNum++);
                Cell answerCell = answerRow.createCell(0);
                answerCell.setCellValue("Ans: " + question.getCorrectAnswer());
                answerCell.setCellStyle(style);

                // Note: We removed the empty row here as per your request
            }

            // Resize all columns to fit the content size
            for (int i = 0; i < questions.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create Excel file", e);
        }
    }
}
