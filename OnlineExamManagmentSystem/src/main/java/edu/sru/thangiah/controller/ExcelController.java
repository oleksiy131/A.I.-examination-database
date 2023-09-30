package edu.sru.thangiah.controller;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.StudentRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/*
 *____  __    __        _ _ 
 / __ \/ /__ / /__ ___ (_|_)
/ /_/ / / -_)  '_/(_-</ / / 
\____/_/\__/_/\_\/___/_/_/  
                        
 */

@Controller
public class ExcelController {
    
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CourseRepository courseRepository;

    public ExcelController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "redirect:/import?error=emptyfile";
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            String courseName = sheet.getRow(1).getCell(0).getStringCellValue();
            Long instructorId = (long) sheet.getRow(3).getCell(1).getNumericCellValue();
            Long courseId = (long) sheet.getRow(4).getCell(1).getNumericCellValue();

            // Create and save course
            Course course = new Course();
            course.setId(courseId);
            course.setCourseName(courseName);
            courseRepository.save(course);

            // Create and save instructor
            Instructor instructor = new Instructor();
            instructor.setInstructorId(instructorId);
            instructor.setInstructorFirstName(sheet.getRow(3).getCell(2).getStringCellValue());
            instructor.setInstructorLastName(sheet.getRow(3).getCell(3).getStringCellValue());
            instructorRepository.save(instructor);
            
            // Associate instructor with the course
            course.setInstructor(instructor);
            courseRepository.save(course);

         // Create and save students
            for (int i = 5; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {  // Add this null check
                    Student student = new Student();
                    if (row.getCell(1) != null) {
                        student.setStudentId((long) row.getCell(1).getNumericCellValue());
                    }
                    if (row.getCell(2) != null) {
                        student.setStudentFirstName(row.getCell(2).getStringCellValue());
                    }
                    if (row.getCell(3) != null) {
                        student.setStudentLastName(row.getCell(3).getStringCellValue());
                    }

                 // Check if a student with the same ID already exists
                    if (student.getStudentId() != null) {
                        Optional<Student> existingStudent = studentRepository.findById(student.getStudentId());
                        if (!existingStudent.isPresent()) {
                            studentRepository.save(student);
                        }
                    } else {
                        System.out.println("Console LOG: Student Id is present in the database");
                    }



                }
            }

            return "redirect:/upload-success";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }
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
