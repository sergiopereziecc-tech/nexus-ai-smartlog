package com.nosmoke.nexus_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nosmoke.nexus_ai.model.ErrorLog;

@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long>{

    
}
