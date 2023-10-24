package edu.sru.thangiah.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.ScheduleManager;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.ScheduleManagerRepository;
import edu.sru.thangiah.repository.StudentRepository;

@Controller
public class SearchController {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private ScheduleManagerRepository scheduleManagerRepository;

    @GetMapping("/instructor/search")
    public String searchInstructors(
        @RequestParam("searchType") String searchType,
        @RequestParam("searchParam") String searchParam,
        Model model
    ) {
        // Perform the instructor search based on searchType and searchParam
        if ("id".equalsIgnoreCase(searchType)) {
            try {
                Long instructorId = Long.parseLong(searchParam);
                Instructor instructor = instructorRepository.findById(instructorId).orElse(null);
                if (instructor != null) {
                    model.addAttribute("instructors", List.of(instructor));
                } else {
                    model.addAttribute("instructors", List.of());
                }
            } catch (NumberFormatException e) {
                model.addAttribute("instructors", List.of());
            }
        } else if ("name".equalsIgnoreCase(searchType)) {
            List<Instructor> instructors = instructorRepository.findByInstructorFirstNameContaining(searchParam);
            model.addAttribute("instructors", instructors);
        } else if ("username".equalsIgnoreCase(searchType)) {
            List<Instructor> instructors = instructorRepository.findByInstructorUsernameContaining(searchParam);
            model.addAttribute("instructors", instructors);
        } else {
            model.addAttribute("instructors", List.of());
        }

        return "instructor-list";
    }
    
    @GetMapping("/schedule-manager/search")
    public String searchScheduleManagers(
        @RequestParam("searchType") String searchType,
        @RequestParam("searchParam") String searchParam,
        Model model
    ) {
        // Perform the schedule manager search based on searchType and searchParam
        if ("id".equalsIgnoreCase(searchType)) {
            try {
                Long managerId = Long.parseLong(searchParam);
                ScheduleManager manager = scheduleManagerRepository.findById(managerId).orElse(null);
                if (manager != null) {
                    model.addAttribute("scheduleManagers", List.of(manager));
                } else {
                    model.addAttribute("scheduleManagers", List.of());
                }
            } catch (NumberFormatException e) {
                model.addAttribute("scheduleManagers", List.of());
            }
        } else if ("name".equalsIgnoreCase(searchType)) {
            List<ScheduleManager> managers = scheduleManagerRepository.findByManagerFirstNameContaining(searchParam);
            model.addAttribute("scheduleManagers", managers);
        } else if ("username".equalsIgnoreCase(searchType)) {
            List<ScheduleManager> managers = scheduleManagerRepository.findByManagerUsernameContaining(searchParam);
            model.addAttribute("scheduleManagers", managers);
        } else {
            model.addAttribute("scheduleManagers", List.of());
        }

        return "schedule-manager-list";
    }
    
    


    @GetMapping("/student/search")
    public String searchStudents(
        @RequestParam("searchType") String searchType,
        @RequestParam("searchParam") String searchParam,
        Model model
    ) {
        // Perform the student search based on searchType and searchParam
        if ("id".equalsIgnoreCase(searchType)) {
            try {
                Long studentId = Long.parseLong(searchParam);
                Student student = studentRepository.findById(studentId).orElse(null);
                if (student != null) {
                    model.addAttribute("students", List.of(student));
                } else {
                    model.addAttribute("students", List.of()); // No matching students
                }
            } catch (NumberFormatException e) {
                model.addAttribute("students", List.of()); // Invalid input
            }
        } else if ("name".equalsIgnoreCase(searchType)) {
            List<Student> students = studentRepository.findBystudentFirstNameContaining(searchParam);
            model.addAttribute("students", students);
        } else if ("username".equalsIgnoreCase(searchType)) {
            List<Student> students = studentRepository.findBystudentUsernameContaining(searchParam);
            model.addAttribute("students", students);
        } else {
            model.addAttribute("students", List.of()); 
        }

        return "student-list"; 
    }
}
