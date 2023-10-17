package edu.sru.thangiah.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Exam;
import edu.sru.thangiah.domain.ExamDetails;
import edu.sru.thangiah.domain.ExamQuestion;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.ExamRepository;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.StudentRepository;
import edu.sru.thangiah.service.ExamQuestionService;

@Controller
@RequestMapping("/instructor")
public class InstructorController {
	
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
        List<ExamQuestion> examQuestions = examQuestionService.getAllExamQuestions();
        model.addAttribute("examQuestions", examQuestions);
        return "listExamQuestions";
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
    public String saveExamQuestion(@ModelAttribute("examQuestion") ExamQuestion examQuestion) {
        examQuestionService.saveExamQuestion(examQuestion);
        return "redirect:/instructor/exam-questions";
    }

    @GetMapping("/exam-questions/delete/{id}")
    public String deleteExamQuestion(@PathVariable Long id) {
        examQuestionService.deleteExamQuestion(id);
        return "redirect:/instructor/exam-questions";
    }

    @GetMapping("/exam/select-questions")
    public String selectExamQuestions(Model model) {
        List<ExamQuestion> examQuestions = examQuestionService.getAllExamQuestions();
        model.addAttribute("examQuestions", examQuestions);
        model.addAttribute("examDetails", new ExamDetails());
        return "selectExamQuestions";
    }
    
    @PostMapping("/exam/generate")
    public String generateExam(@ModelAttribute("examDetails") ExamDetails examDetails) {
        List<Long> selectedExamQuestionIds = examDetails.getSelectedExamQuestionIds();

        // Fetch selected questions from the database
        List<ExamQuestion> selectedQuestions = selectedExamQuestionIds.stream()
            .map(examQuestionService::getExamQuestionById)
            .collect(Collectors.toList());

        // Create a new exam and set its properties
        Exam exam = new Exam();
        exam.setExamName(examDetails.getExamName());
        exam.setDurationInMinutes(examDetails.getExamDuration());
        exam.setQuestions(selectedQuestions);

        // Save the exam to the database
        examRepository.save(exam);

        return "redirect:/instructor/exam-questions"; // Redirect to exam questions list
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
	
	@GetMapping("/edit-student/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
		Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        
        model.addAttribute("student", student);
        return "edit-student";
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
        return "edit-confirmation";
    }
	
	@GetMapping("/student/delete/{id}")
    public String deleteStudent(@PathVariable("id") long id, Model model) {
        Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        studentRepository.delete(student);
        return "edit-confirmation";
    }
	
	

   
}


