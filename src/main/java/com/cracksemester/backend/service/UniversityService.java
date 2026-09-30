package com.cracksemester.backend.service;

import com.cracksemester.backend.entity.University;

import java.util.List;

public interface UniversityService {

    List<University> getAllUniversities();

    University saveUniversity(University university);

}