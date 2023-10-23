package edu.sru.thangiah.controller;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
	private PasswordEncoder passwordEncoder;

    public ExcelController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    @GetMapping("/class-import")
    public String showClassImportPage() {
        return "class-import";
    }
    
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
        	
            return "redirect:/import?error=emptyfile";
        }
        
        	InputStream is = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(is);
        	
            Sheet sheet = workbook.getSheetAt(0);
            String courseNameFull = sheet.getRow(4).getCell(0).getStringCellValue();
            String instructorNameFull = sheet.getRow(2).getCell(0).getStringCellValue();
            String instructorIdString = sheet.getRow(3).getCell(0).getStringCellValue();
            String courseIdString = sheet.getRow(5).getCell(0).getStringCellValue();
            
            System.out.println(instructorIdString);
            System.out.println(instructorNameFull);
            System.out.println(courseNameFull);
            System.out.println(courseIdString);
            
            // converting courseId
            String courseIdShort = removeBeforeColon(courseIdString);            
            long courseId = Long.parseLong(courseIdShort);
            
            System.out.println(courseId);
            String courseName = removeBeforeColon(courseNameFull); 
            System.out.println(courseName);
            
            // Create and save course
            Course course = new Course();
            course.setId(courseId);
            course.setCourseName(courseName);
            courseRepository.save(course);
            
            String parsedInstructorName = removeBeforeColon(instructorNameFull);
            String[] instructorsNames = extractNames(parsedInstructorName);
            String InsFirstName = instructorsNames[1];
            String InsLastName = instructorsNames[0];
            
            //converting instructorId
            String instructorIdshort = removeBeforeColon(instructorIdString);
            long instructorId = Long.parseLong(instructorIdshort);

            System.out.println(instructorId);
            System.out.println(InsFirstName);
            System.out.println(InsLastName);
            
            Instructor instructor = new Instructor();

            Optional<Instructor> existingInstructor = instructorRepository.findById(instructorId);
            if (!existingInstructor.isPresent()) {

            	return "redirect:/upload-fail";
            } else {
                // Associate instructor with the course
            	instructor = existingInstructor.get();
            	course.setInstructor(instructor);
            	courseRepository.save(course);
            }
            
            //this section adds the students id's, names, emails, generates usernames and default password
            
            try {
                for (int i = 8; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {  // null check
                        System.out.println("Encountered null row. Stopping processing.");
                        break; // This will exit the loop when a null row is encountered.
                    }
                    
                    Student student = new Student();
                    String StuFirstName;
                    String StuLastName;
                    long StudentID;
                    String studentEmail;
                    
                    if (row.getCell(0) != null) {
                        StudentID = (long) row.getCell(0).getNumericCellValue();
                        System.out.println(StudentID);
                        student.setStudentId(StudentID);
                    }
                    
                    if (row.getCell(1) != null) {
                        String StudentName = row.getCell(1).getStringCellValue();
                        String[] StudentNames = extractNames(StudentName);
                        StuFirstName = StudentNames[1];
                        StuLastName = StudentNames[0];
                        student.setStudentFirstName(StuFirstName);
                        student.setStudentLastName(StuLastName);
                    }
                    if (row.getCell(2) != null) {
                        studentEmail = row.getCell(2).getStringCellValue();
                        System.out.println(studentEmail);
                        student.setStudentEmail(studentEmail);
                        String username = parseEmail(studentEmail);
                        student.setStudentUsername(username);
                        System.out.println(username);
                    }
                    
                    Roles roles = roleRepository.findById(2L).orElseThrow(() -> new RuntimeException("Role with ID 4 not found"));
            		List<Roles> rolesList = new ArrayList<>();
            		rolesList.add(roles);
            		student.setRoles(rolesList);

                    student.setStudentPassword("student");
                    String hashedPassword = passwordEncoder.encode(student.getStudentPassword());
                    student.setStudentPassword(hashedPassword);

                    // Check if the student already exists in the database
                    Optional<Student> existingStudent = studentRepository.findById(student.getStudentId());
                    if (!existingStudent.isPresent()) {
                        // Student does not exist, save it
                        studentRepository.save(student);

                        // Associate the student with the course
                        student.getCourses().add(course);
                        studentRepository.save(student);
                    } else {
                        System.out.println("Console LOG: Student Id is already present in the database");
                    }
                    
                    Optional<User> existingUser = userRepository.findById(student.getStudentId());
                    if (!existingUser.isPresent()) {
	                    User user = new User();
	                    user.setEmail(student.getStudentEmail());
	                    user.setFirstName(student.getStudentFirstName());
	                    user.setLastName(student.getStudentLastName());
	                    user.setUsername(student.getStudentUsername());
	                    user.setPassword(hashedPassword);
	                    user.setEnabled(true);
	                    rolesList.add(roles);
	            		user.setRoles(rolesList);
	                    userRepository.save(user);
                    }
                    else {
                    	System.out.println("Console LOG: User Id is already present in the database");
                    }                    
                    
                }

                return "redirect:/upload-success";

            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/error";
            }
        }
    
    public static String removeBeforeColon(String input) {
        int colonIndex = input.indexOf(":");
        if (colonIndex != -1) {
            return input.substring(colonIndex + 1).trim();
        } else {
            // Return the original string if there is no colon in it.
            return input;
        }
    }
    
    public static String[] extractNames(String fullName) {
        // Split the full name using a comma and space as the delimiter
        String[] names = fullName.split(",\\s+");
        if (names.length == 2) {
            return names; // Assuming the input format is correct (Last Name, First Name)
        } else {
            // Handle incorrect input format or provide default values as needed
            return new String[]{"null", "null"};
        }
    }
    
    public static String parseEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex != -1) {
            return email.substring(0, atIndex).trim();
        } else {
            // If there is no "@" symbol, return the original email.
            return email;
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
