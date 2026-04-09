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

    //Optional
    //Define as TEXT, String might be not enough to hold the whole message
    @Column(columnDefinition = "TEXT")
    private String stackTrace;
    
    //Optional
    private String contextMetadata;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Environment environment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Level level;

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    public enum Environment {PROD, DEV, STAGING}
    public enum Level { ERROR , WARN , INFO}
    public enum Status{ PENDING, ANALYZING, SOLVED}

    


    
}
