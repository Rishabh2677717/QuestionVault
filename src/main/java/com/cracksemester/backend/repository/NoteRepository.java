package com.cracksemester.backend.repository;

import com.cracksemester.backend.entity.Note;
import com.cracksemester.backend.enums.NoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, String>, JpaSpecificationExecutor<Note> {
    List<Note> findBySubjectCodeIgnoreCaseAndStatus(String subjectCode, NoteStatus status);
    Long countByStatus(NoteStatus status);
}