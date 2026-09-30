package com.cracksemester.backend.service;

import com.cracksemester.backend.dto.QuestionPaperDTO;
import com.cracksemester.backend.dto.NoteDTO;

import java.util.Map;

public interface FavoriteService {
    Map<String, Object> getUserFavorites(String email);
    void addFavorite(String email, String resourceType, String resourceId);
    void removeFavorite(String email, String resourceType, String resourceId);
}
