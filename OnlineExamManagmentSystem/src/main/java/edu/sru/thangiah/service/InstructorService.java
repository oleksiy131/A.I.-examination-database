package edu.sru.thangiah.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.exception.ResourceNotFoundException;
import edu.sru.thangiah.repository.InstructorRepository;

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
 

    public void saveAll(List<Instructor> instructors) {
        instructorRepository.saveAll(instructors);
    }
    
    public void save(Instructor instructor) {
        instructorRepository.save(instructor);
    }
    
}
