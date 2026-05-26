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
@Table(name = "attempt_answers")
public class AttemptAnswer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long attemptId;

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false, length = 4000)
    private String answerText = "";

    @Column(nullable = false)
    private Boolean correct = false;

    @Column(nullable = false)
    private Integer score = 0;

    @Column(nullable = false)
    private Boolean reviewed = false;
}

