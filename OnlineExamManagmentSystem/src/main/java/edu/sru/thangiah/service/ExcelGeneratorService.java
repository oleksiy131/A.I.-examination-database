// src/main/java/edu/sru/thangiah/service/ExcelGeneratorService.java

package edu.sru.thangiah.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ExcelGeneratorService {

    public byte[] generateExcel(List<Map<String, Object>> quizData) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Quiz");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Question Number");
        headerRow.createCell(1).setCellValue("Question");
        headerRow.createCell(2).setCellValue("Choices");

        // Create data rows
        for (int i = 0; i < quizData.size(); i++) {
            Map<String, Object> question = quizData.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue((String) question.get("question"));

            @SuppressWarnings("unchecked")
            List<String> choices = (List<String>) question.get("choices");
            row.createCell(2).setCellValue(String.join(", ", choices));
        }

        // Write workbook to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        return byteArrayOutputStream.toByteArray();
    }
}
