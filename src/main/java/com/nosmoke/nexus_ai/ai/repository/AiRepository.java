package com.nosmoke.nexus_ai.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nosmoke.nexus_ai.model.AiSolutions;
import java.util.List;
import com.nosmoke.nexus_ai.model.ErrorLog;

@Repository
public interface AiRepository extends JpaRepository<AiSolutions, Long>{
    

    public List<AiSolutions> findByErrorLog(ErrorLog errorLog);
}
