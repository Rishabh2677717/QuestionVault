package com.cracksemester.backend.service;

import com.cracksemester.backend.dto.PYQRequest;
import com.cracksemester.backend.entity.PYQ;
import com.cracksemester.backend.entity.Subject;
import com.cracksemester.backend.repository.PYQRepository;
import com.cracksemester.backend.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PYQService {

    private final PYQRepository pyqRepository;
    private final SubjectRepository subjectRepository;

    public PYQService(
            PYQRepository pyqRepository,
            SubjectRepository subjectRepository
    ) {
        this.pyqRepository = pyqRepository;
        this.subjectRepository = subjectRepository;
    }

    // Get all PYQs
    public List<PYQ> getAllPYQs() {
        return pyqRepository.findAll();
    }

    // Get PYQs by subject
    public List<PYQ> getPYQsBySubject(String subjectId) {
        return pyqRepository.findBySubjectId(subjectId);
    }

    // Save PYQ
    public PYQ save(PYQRequest request) {

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() ->
                        new RuntimeException("Subject not found")
                );

        PYQ pyq = new PYQ();

        pyq.setSubject(subject);
        pyq.setYear(request.getYear());
        pyq.setDescription(request.getDescription());
        pyq.setFileUrl(request.getFileUrl());

        return pyqRepository.save(pyq);
    }

    // Delete PYQ
    public void deletePYQ(String id) {

        if (!pyqRepository.existsById(id)) {
            throw new RuntimeException("PYQ not found");
        }

        pyqRepository.deleteById(id);
    }

    // Update PYQ
    public PYQ update(String id, PYQRequest request) {

        PYQ pyq = pyqRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("PYQ not found")
                );

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() ->
                        new RuntimeException("Subject not found")
                );

        pyq.setSubject(subject);
        pyq.setYear(request.getYear());
        pyq.setDescription(request.getDescription());
        pyq.setFileUrl(request.getFileUrl());

        return pyqRepository.save(pyq);
    }
}