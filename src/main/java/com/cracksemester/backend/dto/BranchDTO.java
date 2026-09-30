package com.cracksemester.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchDTO {
    private String id;
    private String name;
    private String shortName;
    private String code;
    private String description;
    private Long subjectCount;
    private Long pyqCount;
    private Long notesCount;
    private String status;
}
