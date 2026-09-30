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
public class SubjectDTO {
    private String id;
    private String code;
    private String name;
    private String branch;
    private Integer semester;
    private Integer yearId;
    private String description;
    private List<String> syllabus;
    private List<String> importantTopics;
    private Long pyqCount;
    private Long notesCount;
    private Boolean popular;
    private String status;
}
