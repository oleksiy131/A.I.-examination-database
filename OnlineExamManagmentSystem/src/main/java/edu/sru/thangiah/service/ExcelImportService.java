package edu.sru.thangiah.service;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.repository.StudentRepository;

@Service
public class ExcelImportService {

    private final StudentRepository studentRepository;

    public ExcelImportService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void importStudentsFromExcel(MultipartFile file) throws IOException {
       
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            for (Row row : sheet) {
                // Skip the header row
                if (row.getRowNum() == 0) {
                    continue;
                }

                Student student = new Student();
                student.setStudentFirstName(row.getCell(0).getStringCellValue());
                student.setStudentLastName(row.getCell(1).getStringCellValue());
                student.setStudentEmail(row.getCell(2).getStringCellValue());
                student.setStudentPassword(row.getCell(3).getStringCellValue());
                student.setStudentUsername(row.getCell(4).getStringCellValue());
                student.setCreditsTaken((float) row.getCell(5).getNumericCellValue());

                // Save the student to the database
                studentRepository.save(student);
            }
        }
    }
}
