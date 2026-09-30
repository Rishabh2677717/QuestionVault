package com.cracksemester.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoteRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Subject Code is required")
    private String subjectCode;

    private String subjectName;

    @NotBlank(message = "Branch is required")
    private String branch;

    @NotNull(message = "Semester is required")
    private Integer semester;

    private String unit = "All Units (1-5)";
    private String noteType = "Revision Notes";
    private String author = "QuestionVault Faculty";
    private Integer pages = 24;
    private String description;
    private String status = "PUBLISHED";
}