package edu.sru.thangiah.service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.TeachingClass;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.TeachingClassRepository;


@Service
@Transactional
public class InitDataService {

    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private TeachingClassRepository teachingClassRepository;

    public void initData() {
        Instructor instructor1 = new Instructor("Instructor1");
        Instructor instructor2 = new Instructor("Instructor2");

        TeachingClass class1 = new TeachingClass("Math");
        TeachingClass class2 = new TeachingClass("Science");

        instructor1.addTeachingClass(class1);
        instructor1.addTeachingClass(class2);
        instructor2.addTeachingClass(class1);

        instructorRepository.save(instructor1);
        instructorRepository.save(instructor2);
    }
}

