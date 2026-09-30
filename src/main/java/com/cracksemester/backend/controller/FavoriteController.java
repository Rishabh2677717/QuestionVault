package com.cracksemester.backend.controller;

import com.cracksemester.backend.dto.ApiResponse;
import com.cracksemester.backend.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserFavorites(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthenticated"));
        }
        return ResponseEntity.ok(ApiResponse.success(favoriteService.getUserFavorites(authentication.getName())));
    }

    @PostMapping("/{type}/{id}")
    public ResponseEntity<ApiResponse<String>> addFavorite(
            Authentication authentication,
            @PathVariable String type,
            @PathVariable String id
    ) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthenticated"));
        }
        favoriteService.addFavorite(authentication.getName(), type, id);
        return ResponseEntity.ok(ApiResponse.success("Added to favorites", id));
    }

    @DeleteMapping("/{type}/{id}")
    public ResponseEntity<ApiResponse<String>> removeFavorite(
            Authentication authentication,
            @PathVariable String type,
            @PathVariable String id
    ) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthenticated"));
        }
        favoriteService.removeFavorite(authentication.getName(), type, id);
        return ResponseEntity.ok(ApiResponse.success("Removed from favorites", id));
    }
}
