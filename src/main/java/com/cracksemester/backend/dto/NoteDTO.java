package com.cracksemester.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {
    private String id;
    private String title;
    private String subjectId;
    private String subjectCode;
    private String subjectName;
    private String branch;
    private Integer semester;
    private String unit;
    private String noteType;
    private String author;
    private Integer pages;
    private String fileUrl;
    private String fileName;
    private String fileSize;
    private Integer downloads;
    private Integer views;
    private String status;
    private String description;
    private String updatedDate;
}
