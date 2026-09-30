package com.cracksemester.backend.controller;

import com.cracksemester.backend.entity.University;
import com.cracksemester.backend.service.UniversityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
@CrossOrigin(origins = "http://localhost:5173")
public class UniversityController {

    private final UniversityService service;

    public UniversityController(UniversityService service) {
        this.service = service;
    }

    @GetMapping
    public List<University> getAll() {
        return service.getAllUniversities();
    }

    @PostMapping
    public University save(@RequestBody University university) {
        return service.saveUniversity(university);
    }
}