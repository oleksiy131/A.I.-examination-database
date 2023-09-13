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
        headerRow.createCell(3).setCellValue("Email");
        headerRow.createCell(4).setCellValue("Password");
        headerRow.createCell(5).setCellValue("UserName");
        headerRow.createCell(6).setCellValue("Credits Taken");




      

        int rowNum = 1;
        for (Student student : students) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(student.getStudentId());
            dataRow.createCell(1).setCellValue(student.getStudentFirstName());
            dataRow.createCell(2).setCellValue(student.getStudentLastName());
            dataRow.createCell(3).setCellValue(student.getStudentEmail());
            dataRow.createCell(4).setCellValue(student.getStudentPassword());
            dataRow.createCell(5).setCellValue(student.getStudentUsername());
            dataRow.createCell(6).setCellValue(student.getCreditsTaken());




        }

        // Saves the workbook to a file
        String filePath = "C:\\Users\\oleks\\OneDrive\\Documents\\Fall2023\\Software Engineering\\Reports\\student_data.xlsx";
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }

        // Closes the workbook
        workbook.close();
    }
}
