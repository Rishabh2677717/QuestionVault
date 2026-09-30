package com.cracksemester.backend.entity;

import com.cracksemester.backend.enums.QuestionPaperStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "question_papers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionPaper {

    @Id
    @Column(length = 100)
    private String id;

    @Column(nullable = false, length = 255)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "subject_code", nullable = false, length = 20)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false, length = 255)
    private String subjectName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Column(name = "exam_year", nullable = false)
    private Integer examYear;

    @Column(name = "exam_type", nullable = false, length = 50)
    private String examType;

    @Column(name = "paper_type", length = 50)
    private String paperType;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_size", length = 50)
    private String fileSize;

    @Builder.Default
    private Integer pages = 4;

    @Builder.Default
    private Integer downloads = 0;

    @Builder.Default
    private Integer views = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(length = 30)
    private QuestionPaperStatus status = QuestionPaperStatus.PUBLISHED;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}
