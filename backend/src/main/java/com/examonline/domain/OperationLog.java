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
@Table(name = "operation_logs")
public class OperationLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String actorUsername;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String targetType;

    private String targetId;

    @Column(nullable = false, length = 2000)
    private String detail;
}

