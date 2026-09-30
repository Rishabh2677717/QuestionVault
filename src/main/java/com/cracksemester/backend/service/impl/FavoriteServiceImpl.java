package com.cracksemester.backend.service.impl;

import com.cracksemester.backend.dto.NoteDTO;
import com.cracksemester.backend.dto.QuestionPaperDTO;
import com.cracksemester.backend.entity.Favorite;
import com.cracksemester.backend.entity.User;
import com.cracksemester.backend.repository.FavoriteRepository;
import com.cracksemester.backend.repository.UserRepository;
import com.cracksemester.backend.service.FavoriteService;
import com.cracksemester.backend.service.NoteService;
import com.cracksemester.backend.service.QuestionPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final QuestionPaperService paperService;
    private final NoteService noteService;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserFavorites(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Favorite> favorites = favoriteRepository.findByUserId(user.getId());

        List<QuestionPaperDTO> paperDTOs = new ArrayList<>();
        List<NoteDTO> noteDTOs = new ArrayList<>();

        for (Favorite f : favorites) {
            try {
                if ("papers".equalsIgnoreCase(f.getResourceType()) || "paper".equalsIgnoreCase(f.getResourceType())) {
                    paperDTOs.add(paperService.getQuestionPaperById(f.getResourceId()));
                } else if ("notes".equalsIgnoreCase(f.getResourceType()) || "note".equalsIgnoreCase(f.getResourceType())) {
                    noteDTOs.add(noteService.getNoteById(f.getResourceId()));
                }
            } catch (Exception ignored) {}
        }

        Map<String, Object> result = new HashMap<>();
        result.put("papers", paperDTOs);
        result.put("notes", noteDTOs);
        return result;
    }

    @Override
    @Transactional
    public void addFavorite(String email, String resourceType, String resourceId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String type = resourceType.toLowerCase().startsWith("paper") ? "papers" : "notes";

        if (!favoriteRepository.existsByUserIdAndResourceTypeAndResourceId(user.getId(), type, resourceId)) {
            Favorite favorite = Favorite.builder()
                    .user(user)
                    .resourceType(type)
                    .resourceId(resourceId)
                    .build();
            favoriteRepository.save(favorite);
        }
    }

    @Override
    @Transactional
    public void removeFavorite(String email, String resourceType, String resourceId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String type = resourceType.toLowerCase().startsWith("paper") ? "papers" : "notes";
        favoriteRepository.deleteByUserIdAndResourceTypeAndResourceId(user.getId(), type, resourceId);
    }
}
