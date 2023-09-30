package edu.sru.thangiah.service;

import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public void saveAll(Set<Student> students) {
        studentRepository.saveAll(students);
    }
}
