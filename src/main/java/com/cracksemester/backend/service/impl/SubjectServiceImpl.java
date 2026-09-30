package com.cracksemester.backend.service.impl;

import com.cracksemester.backend.dto.SubjectDTO;
import com.cracksemester.backend.entity.Branch;
import com.cracksemester.backend.entity.Semester;
import com.cracksemester.backend.entity.Subject;
import com.cracksemester.backend.repository.BranchRepository;
import com.cracksemester.backend.repository.NoteRepository;
import com.cracksemester.backend.repository.QuestionPaperRepository;
import com.cracksemester.backend.repository.SemesterRepository;
import com.cracksemester.backend.repository.SubjectRepository;
import com.cracksemester.backend.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final BranchRepository branchRepository;
    private final SemesterRepository semesterRepository;
    private final QuestionPaperRepository paperRepository;
    private final NoteRepository noteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> getSubjects(String branch, Integer semester, String search) {
        List<Subject> subjects = subjectRepository.findAll();

        return subjects.stream()
                .filter(s -> branch == null || branch.equalsIgnoreCase("ALL") || s.getBranch().getId().equalsIgnoreCase(branch) || s.getBranch().getId().equalsIgnoreCase("ALL"))
                .filter(s -> semester == null || semester <= 0 || s.getSemester().getId().equals(semester))
                .filter(s -> search == null || search.trim().isEmpty() ||
                        s.getName().toLowerCase().contains(search.toLowerCase().trim()) ||
                        s.getCode().toLowerCase().contains(search.toLowerCase().trim()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectDTO getSubjectById(String idOrCode) {
        Subject subject = subjectRepository.findById(idOrCode)
                .orElseGet(() -> subjectRepository.findByCodeIgnoreCase(idOrCode)
                        .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + idOrCode)));
        return mapToDTO(subject);
    }

    @Override
    @Transactional
    public SubjectDTO createSubject(SubjectDTO dto) {
        if (subjectRepository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new IllegalArgumentException("Subject Code " + dto.getCode() + " already exists!");
        }

        Branch branch = branchRepository.findById(dto.getBranch() != null ? dto.getBranch() : "CSE")
                .orElseGet(() -> branchRepository.findAll().stream().findFirst().orElseThrow());

        Semester semester = semesterRepository.findById(dto.getSemester() != null ? dto.getSemester() : 3)
                .orElseGet(() -> semesterRepository.findAll().stream().findFirst().orElseThrow());

        Subject subject = Subject.builder()
                .id(dto.getCode().toLowerCase() + "-" + dto.getName().toLowerCase().replaceAll("[^a-z0-9]", "-"))
                .code(dto.getCode().toUpperCase())
                .name(dto.getName())
                .branch(branch)
                .semester(semester)
                .yearId(semester.getId() <= 2 ? 1 : semester.getId() <= 4 ? 2 : semester.getId() <= 6 ? 3 : 4)
                .description(dto.getDescription())
                .popular(dto.getPopular() != null ? dto.getPopular() : false)
                .active(true)
                .build();

        subjectRepository.save(subject);
        return mapToDTO(subject);
    }

    @Override
    @Transactional
    public SubjectDTO updateSubject(String id, SubjectDTO dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + id));

        if (dto.getName() != null) subject.setName(dto.getName());
        if (dto.getDescription() != null) subject.setDescription(dto.getDescription());
        if (dto.getPopular() != null) subject.setPopular(dto.getPopular());

        subjectRepository.save(subject);
        return mapToDTO(subject);
    }

    @Override
    @Transactional
    public void deleteSubject(String id) {
        subjectRepository.deleteById(id);
    }

    private SubjectDTO mapToDTO(Subject s) {
        return SubjectDTO.builder()
                .id(s.getId())
                .code(s.getCode())
                .name(s.getName())
                .branch(s.getBranch() != null ? s.getBranch().getId() : "CSE")
                .semester(s.getSemester() != null ? s.getSemester().getId() : 3)
                .yearId(s.getYearId())
                .description(s.getDescription())
                .syllabus(Arrays.asList(
                        "Unit I: Fundamentals & Core Principles",
                        "Unit II: Intermediate Structural Representations",
                        "Unit III: Advanced Algorithms & Controls",
                        "Unit IV: System Architectures & Applications",
                        "Unit V: Advanced Practicals & Optimization"
                ))
                .importantTopics(Arrays.asList(
                        "Core Algorithm Analysis",
                        "Design Pattern Implementation",
                        "System Optimization Techniques",
                        "Frequently Asked Exam Section-C Questions"
                ))
                .pyqCount(14L)
                .notesCount(8L)
                .popular(s.getPopular())
                .status(s.getActive() ? "ACTIVE" : "INACTIVE")
                .build();
    }
}