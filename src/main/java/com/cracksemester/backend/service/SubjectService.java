package com.cracksemester.backend.service;

import com.cracksemester.backend.dto.SubjectDTO;

import java.util.List;

public interface SubjectService {
    List<SubjectDTO> getSubjects(String branch, Integer semester, String search);
    SubjectDTO getSubjectById(String idOrCode);
    SubjectDTO createSubject(SubjectDTO subjectDTO);
    SubjectDTO updateSubject(String id, SubjectDTO subjectDTO);
    void deleteSubject(String id);
}