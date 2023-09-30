package edu.sru.thangiah.service;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;
    
    public void save(Course course) {
        courseRepository.save(course);
    }

    public void saveAll(List<Course> courses) {
        courseRepository.saveAll(courses);
    }
}
