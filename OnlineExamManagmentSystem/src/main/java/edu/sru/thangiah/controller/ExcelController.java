package edu.sru.thangiah.controller;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.repository.StudentRepository;

import java.io.*;
import java.util.*;

@Controller
public class ExcelController {
    private final StudentRepository studentRepository; // Inject your repository here

    public ExcelController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping("/uploadExcel")
    public String uploadExcel(@RequestParam("file") MultipartFile file) throws InvalidFormatException {
        if (file.isEmpty()) {
            // Handle empty file error
            return "redirect:/import?error=emptyfile";
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Assuming the data is on the first sheet

            // Iterate through rows and columns to extract data
            for (Row row : sheet) {
                Student student = new Student();
                
                // Handle each cell in the row
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                // Cell contains a string value
                                switch (i) {
                                    case 0:
                                        student.setStudentFirstName(cell.getStringCellValue());
                                        break;
                                    case 1:
                                        student.setStudentLastName(cell.getStringCellValue());
                                        break;
                                    case 2:
                                        student.setStudentEmail(cell.getStringCellValue());
                                        break;
                                    case 3:
                                        student.setStudentPassword(cell.getStringCellValue());
                                        break;
                                    case 4:
                                        student.setStudentUsername(cell.getStringCellValue());
                                        break;
                                    // Add cases for other cells as needed
                                }
                                break;
                            case NUMERIC:
                                // Cell contains a numeric value
                                double numericValue = cell.getNumericCellValue();
                                String stringValue = String.valueOf(numericValue);
                                switch (i) {
                                    // Handle numeric value for specific cells if needed
                                }
                                break;
                            // Handle other cell types if necessary (e.g., BOOLEAN, FORMULA)
                        }
                    }
                }

                studentRepository.save(student); // Save the student to the database
            }

            // Redirect to a success page
            return "redirect:/import?success=true";
        } catch (IOException e) {
            e.printStackTrace();
            // Handle file processing error
            return "redirect:/import?error=processing";
        }
    }
}
