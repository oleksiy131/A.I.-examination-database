package edu.sru.thangiah.service;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.StudentRepository;
import jakarta.transaction.Transactional;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;

@Transactional
@Service
public class ExcelParserService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CourseRepository courseRepository;

    public String parseExcelFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "File is empty";
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
        	
        	System.out.println("Inside Excel Parser");

            Sheet sheet = workbook.getSheetAt(0);
            String courseName = sheet.getRow(1).getCell(0).getStringCellValue();
            Long instructorId = (long) sheet.getRow(3).getCell(1).getNumericCellValue();
            Long courseId = (long) sheet.getRow(4).getCell(1).getNumericCellValue();
            
            
            System.out.println("Course Name: " + courseName);
            System.out.println("Instructor ID: " + instructorId);
            System.out.println("Course ID: " + courseId);

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

            // Create and save students
            for (int i = 5; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Student student = new Student();
                student.setStudentId((long) row.getCell(1).getNumericCellValue());
                student.setStudentFirstName(row.getCell(2).getStringCellValue());
                student.setStudentLastName(row.getCell(3).getStringCellValue());

                // Check if a student with the same ID already exists
                Optional<Student> existingStudent = studentRepository.findById(student.getStudentId());
                if (!existingStudent.isPresent()) {
                    // Save the student to the database only if it doesn't exist
                    studentRepository.save(student);
                }
            }

            return "File uploaded and processed successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to upload file: " + e.getMessage();
        }
    }
}
