package com.cracksemester.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterDTO {
    private Integer id;
    private String name;
    private String shortName;
    private String year;
    private Integer yearId;
    private Long subjectsCount;
    private Long pyqCount;
    private Long notesCount;
    private String status;
}
