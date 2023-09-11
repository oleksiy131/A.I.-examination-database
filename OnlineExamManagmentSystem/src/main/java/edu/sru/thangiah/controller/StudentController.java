package edu.sru.thangiah.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.StudentRepository;

@RestController
@RequestMapping("/student/course")
public class StudentController 
{

	private StudentRepository studentRepository;

	private CourseRepository courseRepository;

	public StudentController(StudentRepository studentRepository, CourseRepository courseRepository) 
	{
		super();
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository; 
	}
	
	@PostMapping
	public Student saveStudentWithCourse(@RequestBody Student student) {
		return studentRepository.save(student);
	}
	
	@GetMapping
	public List<Student> findAllStudents() {
		return (List<Student>) studentRepository.findAll();
	}
	
	@GetMapping("/{studentId}")
	public Student findStudent(@PathVariable Long studentId) {
		return studentRepository.findById(studentId).orElse(null);
	}
	
	@GetMapping("/find/{name}")
	public List<Student> findBystudentFirstNameContaining(@PathVariable String name){
		return studentRepository.findBystudentFirstNameContaining(name);
	}
	
	public List<Course> findByIdContaining(@PathVariable Long id){
		return courseRepository.findByIdContaining(id);
	}
	
}
