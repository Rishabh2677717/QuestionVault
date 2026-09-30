package com.cracksemester.backend.controller;

import com.cracksemester.backend.dto.ApiResponse;
import com.cracksemester.backend.dto.NoteDTO;
import com.cracksemester.backend.dto.QuestionPaperDTO;
import com.cracksemester.backend.dto.SubjectDTO;
import com.cracksemester.backend.service.NoteService;
import com.cracksemester.backend.service.QuestionPaperService;
import com.cracksemester.backend.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SubjectService subjectService;
    private final QuestionPaperService paperService;
    private final NoteService noteService;

    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchSuggestions(@RequestParam("q") String query) {
        Map<String, Object> response = new HashMap<>();

        if (query == null || query.trim().length() < 2) {
            response.put("subjects", List.of());
            response.put("papers", List.of());
            response.put("notes", List.of());
            response.put("branches", List.of());
            return ResponseEntity.ok(ApiResponse.success(response));
        }

        List<SubjectDTO> matchingSubjects = subjectService.getSubjects(null, null, query);
        List<QuestionPaperDTO> matchingPapers = paperService.getQuestionPapers(0, 5, null, null, null, null, "PUBLISHED", query, "newest").getContent();
        List<NoteDTO> matchingNotes = noteService.getNotes(0, 5, null, null, null, "PUBLISHED", query).getContent();

        List<Map<String, String>> subjectItems = matchingSubjects.stream().map(s -> Map.of(
                "type", "Subject",
                "title", s.getCode() + " — " + s.getName(),
                "subtitle", s.getBranch() + " · Sem " + s.getSemester(),
                "url", "/subjects/" + s.getId()
        )).collect(Collectors.toList());

        List<Map<String, String>> paperItems = matchingPapers.stream().map(p -> Map.of(
                "type", "Paper",
                "title", p.getSubjectCode() + " " + p.getSubjectName() + " (" + p.getYear() + ")",
                "subtitle", p.getExamType() + " · " + p.getBranch() + " Sem " + p.getSemester(),
                "url", "/question-papers/" + p.getId()
        )).collect(Collectors.toList());

        List<Map<String, String>> noteItems = matchingNotes.stream().map(n -> Map.of(
                "type", "Note",
                "title", n.getSubjectCode() + " " + n.getTitle(),
                "subtitle", n.getNoteType(),
                "url", "/notes/" + n.getId()
        )).collect(Collectors.toList());

        response.put("subjects", subjectItems);
        response.put("papers", paperItems);
        response.put("notes", noteItems);
        response.put("branches", List.of());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
