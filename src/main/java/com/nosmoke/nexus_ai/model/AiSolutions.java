package com.nosmoke.nexus_ai.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ai_solutions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AiSolutions {
//id: SERIAL PRIMARY KEY.

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


// log_id: INTEGER (La Foreign Key que apunta al ID del error).

@ManyToOne
@JoinColumn(name = "log_id")
private ErrorLog errorLog;
// solution_text: TEXT (La propuesta de la IA).


private String solutionText;

// confidence_score: DECIMAL (¿Qué tan segura está la IA del 0 al 100?).

private Integer confidenceScore;

// created_at: TIMESTAMP.
@CreationTimestamp
private LocalDateTime timestamp;



}
