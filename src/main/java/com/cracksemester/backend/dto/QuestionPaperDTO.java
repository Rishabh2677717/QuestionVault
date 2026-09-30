package com.cracksemester.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPaperDTO {
    private String id;
    private String title;
    private String subjectId;
    private String subjectCode;
    private String subjectName;
    private String branch;
    private Integer semester;
    private String academicYear;
    private Integer year;
    private String examType;
    private String paperType;
    private String fileUrl;
    private String fileName;
    private String fileSize;
    private Integer pages;
    private Integer downloads;
    private Integer views;
    private String status;
    private String description;
    private List<String> tags;
    private String uploadedBy;
    private String uploadedDate;
}
