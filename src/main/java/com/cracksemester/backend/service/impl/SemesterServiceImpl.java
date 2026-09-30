package com.cracksemester.backend.service.impl;

import com.cracksemester.backend.dto.SemesterDTO;
import com.cracksemester.backend.entity.Semester;
import com.cracksemester.backend.repository.SemesterRepository;
import com.cracksemester.backend.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SemesterServiceImpl implements SemesterService {

    private final SemesterRepository semesterRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SemesterDTO> getSemesters() {
        return semesterRepository.findByActiveTrueOrderByIdAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SemesterDTO getSemesterById(Integer id) {
        Semester s = semesterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Semester not found: " + id));
        return mapToDTO(s);
    }

    private SemesterDTO mapToDTO(Semester s) {
        return SemesterDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .shortName(s.getShortName())
                .year(s.getYearGroup())
                .yearId(s.getId() <= 2 ? 1 : s.getId() <= 4 ? 2 : s.getId() <= 6 ? 3 : 4)
                .subjectsCount(6L)
                .pyqCount(48L)
                .notesCount(36L)
                .status(s.getActive() ? "ACTIVE" : "INACTIVE")
                .build();
    }
}