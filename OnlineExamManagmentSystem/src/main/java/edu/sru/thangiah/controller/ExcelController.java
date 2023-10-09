package edu.sru.thangiah.controller;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.model.Roles;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.RoleRepository;
import edu.sru.thangiah.repository.StudentRepository;
import edu.sru.thangiah.repository.UserRepository;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
    private RoleRepository roleRepository;
    
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserRepository userRepository;


    public ExcelController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    @GetMapping("/class-import")
    public String showClassImportPage() {
        return "class-import";
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
                if (row != null) {  //null check
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
                    Optional<Student> existingStudent = studentRepository.findById(student.getStudentId());
                    if (!existingStudent.isPresent()) {
                        studentRepository.save(student);

                        // Associate the student with the course
                        student.getCourses().add(course);  
                        studentRepository.save(student);
                    } else {
                        System.out.println("Console LOG: Student Id is already present in the database");
                    }
                }
            }


            return "redirect:/upload-success";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }

    @Transactional
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
                                    case 1:
                                        student.setStudentFirstName(cell.getStringCellValue());
                                        break;
                                    case 2:
                                        student.setStudentLastName(cell.getStringCellValue());
                                        break;
                                    case 3:
                                        student.setStudentEmail(cell.getStringCellValue());
                                        break;
                                    case 4:
                                        student.setStudentPassword(cell.getStringCellValue());
                                        break;
                                    case 5:
                                        student.setStudentUsername(cell.getStringCellValue());
                                        break;
                                    // Add cases for other cells as needed
                                }
                                break;
                            case NUMERIC:
                                // Cell contains a numeric value
                                double numericValue = cell.getNumericCellValue();
                                switch (i) {
                                    case 0:
                                        student.setStudentId((long) numericValue);
                                        break;
                                    case 6:
                                        // Assuming column 6 contains numeric value for Credits Taken
                                        student.setCreditsTaken((float) numericValue);
                                        break;
                                    // Handle numeric value for other cells if needed
                                }
                                break;
                        }
              
                    }
                }
                
                // Fetch the role with ID 2 and set it to the student
                Roles role = roleRepository.findById(2L)
                    .orElseThrow(() -> new RuntimeException("Role with ID 2 not found"));
                List<Roles> rolesList = new ArrayList<>();
                student.setRoles(rolesList);

                // Save the new instructor
                studentRepository.save(student);

                // Create and save the corresponding user
                User newUser = new User();
                newUser.setId(student.getStudentId());
                newUser.setUsername(student.getStudentUsername());
                newUser.setPassword(student.getStudentPassword());  // We might want to encode this
        		newUser.setRoles(rolesList); 

                // Set enabled for the user as well
                newUser.setEnabled(true);

                userRepository.save(newUser);

                // Check if a student with the same ID already exists
                Optional<Student> existingStudent = studentRepository.findById(student.getStudentId());
                if (!existingStudent.isPresent()) {
                    // Saving the student to the database only if it doesn't exist
                    studentRepository.save(student);
                } else {
                    // Handling the case where the student already exists, if needed
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
