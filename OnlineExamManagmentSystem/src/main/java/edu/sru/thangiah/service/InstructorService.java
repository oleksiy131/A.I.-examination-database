package edu.sru.thangiah.service;

import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.exception.ResourceNotFoundException;
import edu.sru.thangiah.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InstructorService {

    @Autowired
    private InstructorRepository instructorRepository;

    public void deleteInstructor(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
            .orElseThrow(() -> new ResourceNotFoundException("Instructor not found: " + instructorId));
        
        instructorRepository.deleteById(instructorId);
    }
    
}
