
package edu.sru.thangiah.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

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
}
