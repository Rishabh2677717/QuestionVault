package com.cracksemester.backend.service.impl;

import com.cracksemester.backend.entity.University;
import com.cracksemester.backend.repository.UniversityRepository;
import com.cracksemester.backend.service.UniversityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UniversityServiceImpl implements UniversityService {

    private final UniversityRepository repository;

    public UniversityServiceImpl(UniversityRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<University> getAllUniversities() {
        return repository.findAll();
    }

    @Override
    public University saveUniversity(University university) {
        return repository.save(university);
    }
}