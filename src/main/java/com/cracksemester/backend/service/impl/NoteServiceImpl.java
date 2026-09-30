package com.cracksemester.backend.service.impl;

import com.cracksemester.backend.dto.*;
import com.cracksemester.backend.entity.*;
import com.cracksemester.backend.entity.Note;
import com.cracksemester.backend.enums.NoteStatus;
import com.cracksemester.backend.repository.*;
import com.cracksemester.backend.service.NoteService;
import com.cracksemester.backend.service.StorageService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final SubjectRepository subjectRepository;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<NoteDTO> getNotes(int page, int size, String branch, Integer semester, String noteType, String status, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<Note> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (branch != null && !branch.equalsIgnoreCase("ALL")) {
                predicates.add(cb.or(
                        cb.equal(cb.lower(root.get("branch").get("id")), branch.toLowerCase()),
                        cb.equal(cb.lower(root.get("branch").get("id")), "all")
                ));
            }

            if (semester != null && semester > 0) {
                predicates.add(cb.equal(root.get("semester").get("id"), semester));
            }

            if (noteType != null && !noteType.equalsIgnoreCase("ALL")) {
                predicates.add(cb.like(cb.lower(root.get("noteType")), "%" + noteType.toLowerCase() + "%"));
            }

            if (status != null && !status.equalsIgnoreCase("ALL")) {
                try {
                    NoteStatus statusEnum = NoteStatus.valueOf(status.toUpperCase());
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (Exception ignored) {}
            }

            if (search != null && !search.trim().isEmpty()) {
                String q = "%" + search.toLowerCase().trim() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), q),
                        cb.like(cb.lower(root.get("subjectCode")), q),
                        cb.like(cb.lower(root.get("subjectName")), q)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Note> notePage = noteRepository.findAll(spec, pageable);

        List<NoteDTO> content = notePage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PagedResponse.<NoteDTO>builder()
                .content(content)
                .page(notePage.getNumber())
                .size(notePage.getSize())
                .totalElements(notePage.getTotalElements())
                .totalPages(notePage.getTotalPages())
                .last(notePage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public NoteDTO getNoteById(String id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with ID: " + id));
        return mapToDTO(note);
    }

    @Override
    @Transactional
    public NoteDTO createNote(NoteRequest request, MultipartFile file) {
        Subject subject = null;
        if (request.getSubjectCode() != null) {
            subject = subjectRepository.findByCodeIgnoreCase(request.getSubjectCode()).orElse(null);
        }
        if (subject == null) {
            subject = subjectRepository.findAll().stream().findFirst().orElse(null);
        }

        Branch branch = subject != null ? subject.getBranch() : null;
        Semester semester = subject != null ? subject.getSemester() : null;

        String fileUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";
        String fileName = request.getSubjectCode() + "_Notes.pdf";
        String fileSize = "4.2 MB";

        if (file != null && !file.isEmpty()) {
            String storedName = storageService.storeFile(file);
            fileUrl = "/uploads/" + storedName;
            fileName = file.getOriginalFilename();
            fileSize = String.format("%.1f MB", file.getSize() / (1024.0 * 1024.0));
        }

        Note note = Note.builder()
                .id("note-" + System.currentTimeMillis())
                .title(request.getTitle())
                .subject(subject)
                .subjectCode(request.getSubjectCode() != null ? request.getSubjectCode() : (subject != null ? subject.getCode() : "KCS501"))
                .subjectName(request.getSubjectName() != null ? request.getSubjectName() : (subject != null ? subject.getName() : "Subject"))
                .branch(branch)
                .semester(semester)
                .unit(request.getUnit() != null ? request.getUnit() : "All Units (1-5)")
                .noteType(request.getNoteType() != null ? request.getNoteType() : "Revision Notes")
                .author(request.getAuthor() != null ? request.getAuthor() : "QuestionVault Faculty")
                .pages(request.getPages() != null ? request.getPages() : 24)
                .fileUrl(fileUrl)
                .fileName(fileName)
                .fileSize(fileSize)
                .downloads(0)
                .views(0)
                .status(NoteStatus.PUBLISHED)
                .description(request.getDescription())
                .build();

        noteRepository.save(note);
        return mapToDTO(note);
    }

    @Override
    @Transactional
    public NoteDTO updateNote(String id, NoteRequest request, MultipartFile file) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with ID: " + id));

        if (request.getTitle() != null) note.setTitle(request.getTitle());
        if (request.getUnit() != null) note.setUnit(request.getUnit());
        if (request.getNoteType() != null) note.setNoteType(request.getNoteType());
        if (request.getStatus() != null) {
            try {
                note.setStatus(NoteStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (Exception ignored) {}
        }

        if (file != null && !file.isEmpty()) {
            String storedName = storageService.storeFile(file);
            note.setFileUrl("/uploads/" + storedName);
            note.setFileName(file.getOriginalFilename());
            note.setFileSize(String.format("%.1f MB", file.getSize() / (1024.0 * 1024.0)));
        }

        noteRepository.save(note);
        return mapToDTO(note);
    }

    @Override
    @Transactional
    public void deleteNote(String id) {
        noteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void incrementDownload(String id) {
        noteRepository.findById(id).ifPresent(n -> {
            n.setDownloads(n.getDownloads() + 1);
            noteRepository.save(n);
        });
    }

    @Override
    @Transactional
    public void incrementView(String id) {
        noteRepository.findById(id).ifPresent(n -> {
            n.setViews(n.getViews() + 1);
            noteRepository.save(n);
        });
    }

    private NoteDTO mapToDTO(Note n) {
        return NoteDTO.builder()
                .id(n.getId())
                .title(n.getTitle())
                .subjectId(n.getSubject() != null ? n.getSubject().getId() : "")
                .subjectCode(n.getSubjectCode())
                .subjectName(n.getSubjectName())
                .branch(n.getBranch() != null ? n.getBranch().getId() : "CSE")
                .semester(n.getSemester() != null ? n.getSemester().getId() : 5)
                .unit(n.getUnit())
                .noteType(n.getNoteType())
                .author(n.getAuthor())
                .pages(n.getPages())
                .fileUrl(n.getFileUrl())
                .fileName(n.getFileName())
                .fileSize(n.getFileSize())
                .downloads(n.getDownloads())
                .views(n.getViews())
                .status(n.getStatus() != null ? n.getStatus().name() : "PUBLISHED")
                .description(n.getDescription())
                .updatedDate(n.getUpdatedAt() != null ? n.getUpdatedAt().toLocalDate().toString() : "2025-01-01")
                .build();
    }
}
