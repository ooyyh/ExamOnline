package com.examonline.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "exam_attempts")
public class ExamAttempt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long paperId;

    @Column(nullable = false)
    private String paperTitle;

    @Column(nullable = false)
    private String studentUsername;

    @Column(nullable = false)
    private String className;

    @Column(nullable = false)
    private String status;

    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private LocalDateTime deadlineTime;

    @Column(nullable = false)
    private Integer score = 0;

    @Column(nullable = false)
    private Boolean suspicious = false;

    @Column(nullable = false)
    private Integer switchCount = 0;

    @Column(nullable = false)
    private Integer autoSubmitLimit = 3;
}

