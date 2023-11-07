package edu.sru.thangiah.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList; 

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Exam;
import edu.sru.thangiah.domain.ExamDetails;
import edu.sru.thangiah.domain.ExamQuestion;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.ScheduleManager;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.model.Roles;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.ExamRepository;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.RoleRepository;
import edu.sru.thangiah.repository.ScheduleManagerRepository;
import edu.sru.thangiah.repository.StudentRepository;
import edu.sru.thangiah.service.ExamQuestionService;
import edu.sru.thangiah.service.ExcelExportService;
import edu.sru.thangiah.repository.UserRepository;

@Controller
@RequestMapping("/instructor")
public class InstructorController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ScheduleManagerRepository scheduleManagerRepository;
	@Autowired
	private ExcelExportService excelExportService;
	
	@RequestMapping("/instructor_homepage")
	public String showInstructorHomepage() {
		return "instructor_homepage";
	}

    private  InstructorRepository instructorRepository;
    private  CourseRepository courseRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ExamQuestionService examQuestionService;
    
    @Autowired
    private ExamRepository examRepository;

    public InstructorController(InstructorRepository instructorRepository, CourseRepository courseRepository) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
    }
    
    @GetMapping("/exam-questions")
    public String listExamQuestions(Model model) {
        try {
            // Call the service method to read exam questions from the file
            examQuestionService.readExamQuestionsFromFile();
            
            // Fetch exam questions after reading from the file
            List<ExamQuestion> examQuestions = examQuestionService.getAllExamQuestions();
            model.addAttribute("examQuestions", examQuestions);
        } catch (IOException e) {
            // Handle the exception appropriately
            e.printStackTrace();
        }
       
        return "listExamQuestions";
    }
    
    
    
    @GetMapping("/exam-landing-page")
    public String showExamLandingPage() {
        return "exam-landing-page";
    }
    
	@PostMapping("/exam-landing-page")
	public String captureExamLandingPageData(@RequestParam(name = "manual", required = false) String generateManualExam,
			@RequestParam(name = "auto", required = false) String otherAction,
			@RequestParam("examName") String examName, @RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			// @RequestParam("examTime") String examTime,
			@RequestParam("duration") int duration) {
		System.out.println(examName);
		System.out.println(startDate);
		System.out.println(endDate);
		// System.out.println(examTime);
		System.out.println(duration);

//		if (!(duration > 0)) {
//			return "redirect:/instructor/exam-landing-page";
//		} else {
			Exam exam = new Exam();
			exam.setExamName(examName);
			exam.setStartTime(startDate);
			exam.setEndTime(endDate);
			exam.setDurationInMinutes(duration);

			// Save the exam to the database
			examRepository.save(exam);

			Long examId = exam.getId();	
			
			if (generateManualExam != null) {
				return "redirect:/exam/selectChapter";
			} else if (otherAction != null) {
				// Handle the "Other Action" button click
				return "redirect:/instructor/auto-generate";
			} else {
				return "error";
			}

		//}
	}
    
    
    @GetMapping("/auto-generate")
    public String showAutoExamPage() {
        return "automatic-exam-generation";
    }

    @GetMapping("/exam-questions/new")
    public String showExamQuestionForm(Model model) {
        model.addAttribute("examQuestion", new ExamQuestion());
        return "editExamQuestions";
    }

    @GetMapping("/exam-questions/edit/{id}")
    public String editExamQuestion(@PathVariable Long id, Model model) {
        ExamQuestion examQuestion = examQuestionService.getExamQuestionById(id);
        model.addAttribute("examQuestion", examQuestion);
        return "editExamQuestions";
    }

    @PostMapping("/exam-questions/update")
    public String updateExamQuestion(@ModelAttribute ExamQuestion examQuestion) {
        // Check if the question with the given ID exists
        ExamQuestion existingQuestion = examQuestionService.getExamQuestionById(examQuestion.getId());

        if (existingQuestion != null) {
            // Add some logging to see the values being updated
            System.out.println("Updating question with ID: " + existingQuestion.getId());
            System.out.println("Updated Question Text: " + examQuestion.getQuestionText());

            // Update the attributes of the existing question
            existingQuestion.setQuestionText(examQuestion.getQuestionText());
            existingQuestion.setOptionA(examQuestion.getOptionA());
            existingQuestion.setOptionB(examQuestion.getOptionB());
            existingQuestion.setOptionC(examQuestion.getOptionC());
            existingQuestion.setOptionD(examQuestion.getOptionD());
            existingQuestion.setCorrectAnswer(examQuestion.getCorrectAnswer());

            // Save the updated question to the repository
            examQuestionService.saveExamQuestion(existingQuestion);
        } else {
            // Handle the case when the question does not exist (e.g., show an error message)
            // You can also redirect to an error page or take other appropriate action.
            System.out.println("CONSOLE LOG: Question with ID " + examQuestion.getId() + " not found.");
        }

        return "redirect:/exam/selectChapter";
    }



    @GetMapping("/exam-questions/delete/{id}")
    public String deleteExamQuestion(@PathVariable Long id) {
        examQuestionService.deleteExamQuestion(id);
        return "redirect:/instructor/exam-questions";
    }

    @GetMapping("/exam/select-questions")
    public String selectExamQuestions(Model model, RedirectAttributes redirectAttributes) {
        // Fetch all available exam questions to display for selection
        List<ExamQuestion> examQuestions = examQuestionService.getAllExamQuestions();
        model.addAttribute("examQuestions", examQuestions);
        model.addAttribute("examDetails", new ExamDetails());

        // Retrieve flash attributes and check if 'generatedExamId' is present
        Map<String, ?> flashAttributes = redirectAttributes.getFlashAttributes();
        if (flashAttributes.containsKey("generatedExamId")) {
            Long generatedExamId = (Long) flashAttributes.get("generatedExamId");
            String examLink = "http://localhost:8080/exam/" + generatedExamId;
            model.addAttribute("examLink", examLink);
        }

        return "selectExamQuestions"; // This should be your Thymeleaf template for selecting exam questions.
    }

    
    @PostMapping("/exam/generate")
    public String generateExam(@ModelAttribute("examDetails") ExamDetails examDetails, Model model) {
        List<Long> selectedExamQuestionIds = examDetails.getSelectedExamQuestionIds();

        // Fetch selected questions from the database
        List<ExamQuestion> selectedQuestions = selectedExamQuestionIds.stream()
            .map(examQuestionService::getExamQuestionById)
            .collect(Collectors.toList());

        // Create a new exam and set its properties
        Exam exam = new Exam();
        exam.setExamName(examDetails.getExamName());
        exam.setDurationInMinutes(examDetails.getDurationInMinutes());
        exam.setQuestions(selectedQuestions);
     // Save the exam to the database
        Exam savedExam = examRepository.save(exam);

        // Add the generated exam's ID to the model
        model.addAttribute("generatedExamId", savedExam.getId());

        // Also, add the exam questions to the model again as they were before
        List<ExamQuestion> examQuestions = examQuestionService.getAllExamQuestions();
        model.addAttribute("examQuestions", examQuestions);

        // Return the same view which is used for selecting exam questions, not a new one
        return "selectExamQuestions"; // This should be the name of your Thymeleaf template for selecting questions
    }
    @GetMapping("/list")
    public String showInstructorList(Model model) {
        List<Instructor> instructors = (List<Instructor>) instructorRepository.findAll();
        model.addAttribute("instructors", instructors);
        return "instructor-list"; // Create an HTML template for instructor list
    }
    
    @GetMapping("/generate-exam")
    public String showExamGenerator(Model model) {
        return "exam-generator"; // Create an HTML template for exam generation
    }
    
    @GetMapping("/pick-exam")
    public String pickExam(Model model) {
        return "pick-exam"; // Create an HTML template for exam generation
    }

    @PostMapping("/instructor/create")
    public ResponseEntity<String> createInstructor(@RequestBody Instructor instructor) {
        try {
            Instructor savedInstructor = instructorRepository.save(instructor);
            return ResponseEntity.ok("Instructor created successfully. Instructor ID: " + savedInstructor.getInstructorId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating instructor: " + e.getMessage());
        }
    }
    
    @GetMapping("/{instructorId}")
    public Instructor findInstructor(@PathVariable Long instructorId) {
        return instructorRepository.findById(instructorId).orElse(null);
    }
    
	@GetMapping("/students")
	public String showStudentList(Model model) {
		// Retrieve the list of students from the repository
		List<Student> students = (List<Student>) studentRepository.findAll();

		// Add the list of students to the model for rendering in the HTML template
		model.addAttribute("students", students);

		// Return the name of the HTML template to be displayed
		return "student-list";
	}
	
    @GetMapping("/list-smIV")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public String showSMIV(Model model) {
        List<ScheduleManager> ScheduleManager = scheduleManagerRepository.findAll();
        model.addAttribute("ScheduleManager", ScheduleManager);
        return "iv-schedule-manager-list";
    }

	
	@GetMapping("/iv-edit-student/{id}")
    public String showUpdateFormIV(@PathVariable("id") long id, Model model) {
		Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        
        model.addAttribute("student", student);
        return "iv-edit-student";
    }
	
	@PostMapping("/update/{id}")
    public String updateStudent(@PathVariable("id") long id, @Validated Student student, 
      BindingResult result, Model model) {
        if (result.hasErrors()) {
            student.setStudentId(id);
            return "update-user";
        }
        
        // Debugging: Print the received student data
        System.out.println("Received Student Data:");
        System.out.println("ID: " + student.getStudentId());
        System.out.println("First Name: " + student.getStudentFirstName());
        System.out.println("Last Name: " + student.getStudentLastName());
        System.out.println("Email: " + student.getStudentEmail());
        System.out.println("Path Variable ID: " + id);
        
//        student.setStudentId(id);
//        student.setRole(student.getRole());
//        student.setStudentPassword(student.getStudentPassword());
        studentRepository.save(student);
        return "iv-edit-confirmation";
    }
	
	@GetMapping("/student/delete/{id}")
    public String deleteStudent(@PathVariable("id") long id, Model model) {
        Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        
        System.out.println(student.getCourses());
        student.getCourses().clear();
        studentRepository.save(student);
        
        studentRepository.delete(student);
        return "iv-edit-confirmation";
    }
	
	@GetMapping("/iv-student-list")
	public String showStudentsListIV(Model model) {
		// Retrieve the list of students from the repository
		List<Student> students = (List<Student>) studentRepository.findAll();

		// Add the list of students to the model for rendering in the HTML template
		model.addAttribute("students", students);

		// Return the name of the HTML template to be displayed
		return "iv-student-list";
	}
	
	@GetMapping("/enable-disable-student/{id}")
	public String enableDisableStudent(@PathVariable Long id, @RequestParam boolean enabled) {
	    // Find the student by id
	    Optional<Student> studentOpt = studentRepository.findById(id);
	    if (studentOpt.isPresent()) {
	        Student student = studentOpt.get();
	        // Toggle the enabled state
	        boolean newEnabledStatus = !student.isEnabled();
	        student.setEnabled(newEnabledStatus);
	        // Save the updated student
	        studentRepository.save(student);
	        
	        // Now, find the associated User entity and update its enabled status
	        Optional<User> userOpt = userRepository.findByUsername(student.getStudentUsername());
	        if (userOpt.isPresent()) {
	            User user = userOpt.get();
	            user.setEnabled(newEnabledStatus);
	            userRepository.save(user);
	        }
	    }
	    // Redirect back to the student list
	    return "redirect:/instructor/iv-student-list";
	}

    
	@GetMapping("/iv-create-student")
	public String showCreateStudentFormIV() {
		return "iv-create-student"; // This corresponds to the name of your HTML file
	}
	
    @Transactional
	@PostMapping("/createIV")
	public String createIV(@ModelAttribute Student student, RedirectAttributes redirectAttributes) {
	    System.out.println("Inside student-createIV method");
	    try {
	        // Check if the student with the given username already exists
	        if (studentRepository.findByStudentUsername(student.getStudentUsername()).isPresent()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "Student with given username already exists.");
	            return "redirect:/create";
	        }

	        // Fetch the role with ID 2 and set it to the student
	        Roles roles = roleRepository.findById(2L)
	            .orElseThrow(() -> new RuntimeException("Role with ID 2 not found"));
	        List<Roles> rolesList = new ArrayList<>();
            rolesList.add(roles);
            student.setRoles(rolesList);

	        // Save the new student
	        studentRepository.save(student);

	        // Create and save the corresponding user
	        User newUser = new User();
	        newUser.setId(student.getStudentId());
	        newUser.setUsername(student.getStudentUsername());
	        String hashedPassword = passwordEncoder.encode(student.getStudentPassword());
		    newUser.setPassword(hashedPassword);
	        newUser.setRoles(rolesList);


            // Set enabled for the user as well
            newUser.setEnabled(true);

            userRepository.save(newUser);

	        redirectAttributes.addFlashAttribute("successMessage", "Student and corresponding user added successfully.");
	        return "iv-upload-success";
	    } catch (Exception e) {
	        System.out.println("Failed to add student: " + e.getMessage());
	        redirectAttributes.addFlashAttribute("errorMessage", "Failed to add student.");
	        return "redirect:/fail";
	    }
	}
    @PostMapping("/iv-update/{id}")
    public String updateStudentIV(@PathVariable("id") long id, @Validated Student student, 
      BindingResult result, Model model) {
        if (result.hasErrors()) {
            student.setStudentId(id);
            return "iv-update-user";
        }
        
        // Debugging: Print the received student data
        System.out.println("Received Student Data:");
        System.out.println("ID: " + student.getStudentId());
        System.out.println("First Name: " + student.getStudentFirstName());
        System.out.println("Last Name: " + student.getStudentLastName());
        System.out.println("Email: " + student.getStudentEmail());
        System.out.println("Path Variable ID: " + id);
        
//        student.setStudentId(id);
//        student.setRole(student.getRole());
//        student.setStudentPassword(student.getStudentPassword());
        studentRepository.save(student);
        return "iv-edit-confirmation";
    }
    
	@GetMapping("/associateIV")
	public String associateStudentWithCourseFormIV(Model model) {
		// Retrieve the list of students and courses from the repository
		List<Student> students = studentRepository.findAll();
		List<Course> courses = courseRepository.findAll();

		// Add the lists of students and courses to the model for rendering in the HTML
		// template
		model.addAttribute("students", students);
		model.addAttribute("courses", courses);

		// Return the name of the HTML template for the form
		return "iv-associate-students";
	}
	
	@PostMapping("/associateIV")
	public String handleAssociateStudentWithCourse(@RequestParam("studentId") Long studentId, @RequestParam("courseId") Long courseId) {
		System.out.println(studentId);

        
        Student student = studentRepository.findById(studentId).orElse(null);

        Course course = courseRepository.findById(courseId).orElse(null);;
       

       
       if(!(studentId == 2) && !(studentId == null)) {
       	// Associate the student with the course

           student.getCourses().add(course);
           studentRepository.save(student);
           return "iv-student-association-confirmation";
       } 
       else {
			return "iv-student-association-fail";
		}
	}
	
	@GetMapping("/exportStudentIV")
	public ResponseEntity<String> exportStudentDataIV() {
	    try {
	        // Fetches the list of students from the repository
	        List<Student> students = (List<Student>) studentRepository.findAll(); 

	        if (students != null && !students.isEmpty()) {
	            // Get the user's downloads folder
	            String userHome = System.getProperty("user.home");
	            String downloadsDirectory = userHome + File.separator + "Downloads";
	            
	            // Define the file path in the downloads folder
	            String filePath = downloadsDirectory + File.separator + "student_exported_data.xlsx";
	            
	            // Check if the file already exists
	            File file = new File(filePath);
	            boolean fileExists = file.exists();
	            
	            excelExportService.exportStudentData(students, filePath, fileExists);
	            
	            if (fileExists) {
	                return ResponseEntity.ok("Student data updated successfully!");
	            } else {
	                return ResponseEntity.ok("Student data exported successfully!");
	            }
	        } else {
	            return ResponseEntity.ok("No students found to export!");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to export student data!");
	    }
	}
	
	@GetMapping("/uploadExcelIV")
	public String importStudents() {
		return "iv-import"; // This corresponds to the name of your HTML file
	}
	
	@GetMapping("/iv-upload-success")
	public String uploadSuccess() {
		return "iv-upload-success"; // This corresponds to the name of your HTML file
	}
	
	@Transactional
	@PostMapping("/uploadExcelIV")
	public String uploadExcelIV(@RequestParam("file") MultipartFile file) {
	    if (file.isEmpty()) {
	        // Handle empty file error
	        return "redirect:/instructor/iv-import?error=emptyfile";
	    }

	    try (InputStream inputStream = file.getInputStream()) {
	        Workbook workbook = WorkbookFactory.create(inputStream);
	        Sheet sheet = workbook.getSheetAt(0); // Assuming the data is on the first sheet

	        // Fetch the role with ID 2 once
	        Roles role = roleRepository.findById(2L)
	            .orElseThrow(() -> new RuntimeException("Role with ID 2 not found"));
	        List<Roles> rolesList = new ArrayList<>();

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
                                        System.out.println("Assigned Student ID: " + student.getStudentId());  // For debugging purposes

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
                if (student.getStudentId() == null) {
                    System.out.println("Skipped a row due to missing Student ID.");
                    continue;
                }

                student.setRoles(rolesList);

                Optional<Student> existingStudent = studentRepository.findById(student.getStudentId());
                if (!existingStudent.isPresent()) {
                    // Saving the student to the database since it doesn't exist
                    studentRepository.save(student);
                    User newUser = new User();
                    newUser.setId(student.getStudentId());
                    newUser.setUsername(student.getStudentUsername());
                    
                    // Encode the password before saving
                    String encodedPassword = passwordEncoder.encode(student.getStudentPassword());
                    newUser.setPassword(encodedPassword);
                    
                    newUser.setRoles(rolesList); 
                    newUser.setEnabled(true);
                    userRepository.save(newUser);
                } else {
                    // Handle the case when the student already exists, if needed
                }
            }

            workbook.close();  // Close the workbook
            return "redirect:/instructor/iv-upload-success";

        } catch (IOException e) {
            e.printStackTrace();
            // Handle file processing error
            return "redirect:/instructor/iv-import?error=processing";
        }
    }
	
//	@GetMapping("/student/delete/{id}")
//    public String deleteStudentSMV(@PathVariable("id") long id, Model model) {
//        Student student = studentRepository.findById(id)
//          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
//        studentRepository.delete(student);
//        return "iv-edit-confirmation";
//    }
	
	@GetMapping("/iv-upload-fail")
	public String showIvUploadFail() {
		return "iv-upload-fail"; // This corresponds to the name of your HTML file
	}
	
	@GetMapping("/iv-class-import")
	public String showIvClassImport() {
		return "iv-class-import"; // This corresponds to the name of your HTML file
	}
	
	 @PostMapping("/iv-upload")
	    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
	        if (file.isEmpty()) {
	        	
	            return "redirect:/instructor/iv-import?error=emptyfile";
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

	            	return "redirect:/instructor/iv-upload-fail";
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

	                return "redirect:/instructor/iv-upload-success";

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
	    
	    
	    @GetMapping("/iv-account-management")
		public String accountManager(Model model){
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			
			System.out.println(auth.getName());
			System.out.println(auth.getPrincipal());
			System.out.println(auth.getDetails());
			
			String instructorUser = auth.getName();
			
			List<Instructor> instructor = instructorRepository.findByInstructorUsernameContaining(instructorUser);
			
			model.addAttribute("instructor", instructor);
						
			return "iv-account-management";
		}
	    
	    
	    @GetMapping("/iv-edit-current-instructor/{id}")
		public String editingCurrentUser(@PathVariable("id") long id, Model model) {
	    	Instructor instructor = instructorRepository.findById(id)
	    		      .orElseThrow(() -> new IllegalArgumentException("Invalid instructor Id:" + id));
	    		    
	    		    model.addAttribute("instructor", instructor);
		    return "iv-edit-current-instructor"; 
		}
	    
	    
	    @Transactional
		@PostMapping("/iv-edit-instructor/{id}")
		public String saveCurrentUserEdits(@PathVariable("id") long id, @Validated Instructor instructor, 
		  BindingResult result, Model model, @RequestParam(value = "currentPassword", required = false) String currentPassword, @RequestParam(value = "newPassword", required = false) String newInstructorPassword, 
		  @RequestParam(value = "confirmPassword", required = false) String confirmInstructorPassword) {
		    if (result.hasErrors()) {
		        instructor.setInstructorId(id); 
		        return "iv-edit-current-instructor";
		    }
		    
		    
		    // Fetch the user (or create a new one if not found)
		    User user = userRepository.findByUsername(instructor.getInstructorUsername())
		            .orElse(new User());  
		    
		    boolean passwordError = false;

		    // Only process passwords if new password fields are filled
		    if (newInstructorPassword != null && !newInstructorPassword.isEmpty()) {
		        // Verify current password
		        if (currentPassword != null && !passwordEncoder.matches(currentPassword, user.getPassword())) {
		            model.addAttribute("passwordError", "Current password is incorrect");
		            passwordError = true;
		        } 
		        // Check if new passwords match
		        else if (!newInstructorPassword.equals(confirmInstructorPassword)) {
		            model.addAttribute("passwordError", "New passwords do not match");
		            passwordError = true;
		        } 
		        // Process password update if there's no error
		        else {
		            String encryptedPassword = passwordEncoder.encode(newInstructorPassword);
		            instructor.setInstructorPassword(encryptedPassword);
		            user.setPassword(encryptedPassword);
		            userRepository.save(user);  // Save the user to userRepository
		        }
		    }

		    // Save the instructor regardless of password change
		    instructorRepository.save(instructor);  // Save the instructor

		    // If there was a password error, re-display the form
		    if (passwordError) {
		        model.addAttribute("instructor", instructor);
		        return "iv-edit-current-instructor";
		    }

		    // Debugging: Print the received instructor data
		    System.out.println("Received Instructor Data:");
		    System.out.println("ID: " + instructor.getInstructorId());
		    System.out.println("First Name: " + instructor.getInstructorFirstName());
		    System.out.println("Last Name: " + instructor.getInstructorLastName());
		    System.out.println("Email: " + instructor.getInstructorEmail());
		    System.out.println("Path Variable ID: " + id);
		    
		    return "iv-instructor-edit-confirmation"; 
		}
}



