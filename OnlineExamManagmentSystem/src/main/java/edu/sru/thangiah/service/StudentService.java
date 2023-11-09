package edu.sru.thangiah.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public void saveAll(Set<Student> students) {
        studentRepository.saveAll(students);
    }
}
