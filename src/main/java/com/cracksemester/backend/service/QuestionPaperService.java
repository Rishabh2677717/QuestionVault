package com.cracksemester.backend.service;

import com.cracksemester.backend.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionPaperService {
    PagedResponse<QuestionPaperDTO> getQuestionPapers(int page, int size, String branch, Integer semester, Integer year, String examType, String status, String search, String sort);
    QuestionPaperDTO getQuestionPaperById(String id);
    QuestionPaperDTO createQuestionPaper(QuestionPaperRequest request, MultipartFile file);
    QuestionPaperDTO updateQuestionPaper(String id, QuestionPaperRequest request, MultipartFile file);
    void deleteQuestionPaper(String id);
    List<QuestionPaperDTO> getPopularPapers();
    List<QuestionPaperDTO> getRecentPapers();
    void incrementDownload(String id);
    void incrementView(String id);
}
