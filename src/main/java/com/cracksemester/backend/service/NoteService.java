package com.cracksemester.backend.service;

import com.cracksemester.backend.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NoteService {
    PagedResponse<NoteDTO> getNotes(int page, int size, String branch, Integer semester, String noteType, String status, String search);
    NoteDTO getNoteById(String id);
    NoteDTO createNote(NoteRequest request, MultipartFile file);
    NoteDTO updateNote(String id, NoteRequest request, MultipartFile file);
    void deleteNote(String id);
    void incrementDownload(String id);
    void incrementView(String id);
}