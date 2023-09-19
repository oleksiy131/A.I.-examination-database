package edu.sru.thangiah.controller;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.repository.StudentRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Controller
public class ExcelController {
    private final StudentRepository studentRepository;

    public ExcelController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping("/uploadExcel")
    public String uploadExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            // Handle empty file error
            return "redirect:/import?error=emptyfile";
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Assuming the data is on the first sheet

            // Iterate through rows and columns to extract data
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // Skip the header row
                    continue;
                }

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
                                switch (i) {
                                    case 5:
                                        // Assuming column 5 contains numeric value
                                        student.setCreditsTaken((float) numericValue);
                                        break;
                                    // Handle numeric value for other cells if needed
                                }
                                break;
                        }
                    }
                }

                // Check if a student with the same username already exists
                Optional<Student> existingStudent = studentRepository.findByStudentUsername(student.getStudentUsername());
                if (!existingStudent.isPresent()) {
                    // Save the student to the database only if it doesn't exist
                    studentRepository.save(student);
                }
            }

            // Redirect to a success page
            return "redirect:/upload-success";
        } catch (IOException e) {
            e.printStackTrace();
            // Handle file processing error
            return "redirect:/import?error=processing";
        }
    }
}
