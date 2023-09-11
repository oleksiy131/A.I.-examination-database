package edu.sru.thangiah.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.sru.thangiah.domain.TeachingClass;
import edu.sru.thangiah.service.InstructorService;

@RestController
@RequestMapping("/instructors")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @GetMapping("/{instructorId}/teaching-classes")
    public ResponseEntity<Set<TeachingClass>> getTeachingClasses(@PathVariable Long instructorId) {
        Set<TeachingClass> teachingClasses = instructorService.getTeachingClasses(instructorId);
        return ResponseEntity.ok(teachingClasses);
    }
}

