package edu.sru.thangiah.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.domain.Student;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {
    public void exportStudentData(List<Student> students) throws IOException {
     
        Workbook workbook = new XSSFWorkbook();

        
        Sheet sheet = workbook.createSheet("Student Data");

     
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Student ID");
        headerRow.createCell(1).setCellValue("First Name");
        headerRow.createCell(2).setCellValue("Last Name");
      

        int rowNum = 1;
        for (Student student : students) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(student.getStudentId());
            dataRow.createCell(1).setCellValue(student.getStudentFirstName());
            dataRow.createCell(2).setCellValue(student.getStudentLastName());
        }

        // Save the workbook to a file
        String filePath = "C:\\Users\\oleks\\OneDrive\\Documents\\Fall2023\\Software Engineering\\Reports\\student_data.xlsx";
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }

        // Close the workbook
        workbook.close();
    }
}
