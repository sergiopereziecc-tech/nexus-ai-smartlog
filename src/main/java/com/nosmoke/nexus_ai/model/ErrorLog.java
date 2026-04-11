package com.nosmoke.nexus_ai.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cache.annotation.CacheConfig;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "error_log")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String message;

    
    //Define as TEXT, String might be not enough to hold the whole message
    @Column(columnDefinition = "TEXT")
    private String stackTrace;
    
    //Optional
    private String contextMetadata;

    @Column(columnDefinition = "TEXT")
    private String aiSolution;

    @Column(columnDefinition = "TEXT")
    private String aiExplanation;

    @Column
    @Enumerated(EnumType.STRING)
    private Environment environment;

    @Column
    @Enumerated(EnumType.STRING)
    private Level level;

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column
    @Enumerated(EnumType.STRING)
    private Component component;

    public enum Environment {PROD, DEV, STAGING}
    public enum Level { ERROR , WARN , INFO}
    public enum Status{ PENDING, ANALYZING, SOLVED}
    public enum Component {FRONTEND, BACKEND, BOTH}

    


    
}
