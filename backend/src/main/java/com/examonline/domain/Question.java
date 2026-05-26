package com.examonline.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "questions")
public class Question extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String title;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String subject;

    private String courseCode;
    private String courseName;
    private String chapter;
    private String section;
    private String knowledgeModule;

    @Column(nullable = false)
    private String knowledgePoint;

    @Column(nullable = false)
    private String difficulty;

    private String cognitiveLevel;
    private String source;

    @Column(length = 2000)
    private String tagsJson;

    @Column(nullable = false, length = 4000)
    private String optionsJson;

    @Column(nullable = false, length = 1000)
    private String correctAnswer;

    @Column(nullable = false)
    private Integer score = 5;

    private Integer estimatedMinutes;

    private String explanation;
    private String creatorUsername;
}

