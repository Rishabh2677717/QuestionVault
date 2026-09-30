package com.cracksemester.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuestionPaperRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Subject Code is required")
    private String subjectCode;

    private String subjectName;

    @NotBlank(message = "Branch is required")
    private String branch;

    @NotNull(message = "Semester is required")
    private Integer semester;

    private String academicYear = "2025-26";

    @NotNull(message = "Exam Year is required")
    private Integer year;

    @NotBlank(message = "Exam Type is required")
    private String examType;

    private String paperType = "Regular";
    private String description;
    private String status = "PUBLISHED";
    private List<String> tags;
}
