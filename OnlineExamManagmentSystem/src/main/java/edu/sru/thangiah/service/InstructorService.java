package edu.sru.thangiah.service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.TeachingClass;
import edu.sru.thangiah.repository.InstructorRepository;

@Service
public class InstructorService {
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    public Set<TeachingClass> getTeachingClasses(Long instructorId) {
        Optional<Instructor> instructorOptional = instructorRepository.findById(instructorId);
        
        if (instructorOptional.isPresent()) {
            Instructor instructor = instructorOptional.get();
            return instructor.getTeachingClasses();
        } else {
            throw new NoSuchElementException("Instructor not found");
        }
    }
}

