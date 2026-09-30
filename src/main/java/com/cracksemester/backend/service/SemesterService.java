package com.cracksemester.backend.service;

import com.cracksemester.backend.dto.SemesterDTO;

import java.util.List;

public interface SemesterService {
    List<SemesterDTO> getSemesters();
    SemesterDTO getSemesterById(Integer id);
}