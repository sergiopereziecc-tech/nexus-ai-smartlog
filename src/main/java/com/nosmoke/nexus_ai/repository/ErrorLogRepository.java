package com.nosmoke.nexus_ai.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nosmoke.nexus_ai.model.ErrorLog;
import com.nosmoke.nexus_ai.model.ErrorLog.Component;
import com.nosmoke.nexus_ai.model.ErrorLog.Environment;
import com.nosmoke.nexus_ai.model.ErrorLog.Level;
import com.nosmoke.nexus_ai.model.ErrorLog.Status;

import java.util.List;



@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long>{

    public List<ErrorLog> findByApplicationName(String applicationName);
    public List<ErrorLog> findByStatus(Status status);
    public List<ErrorLog> findByLevel(Level level);
    public List<ErrorLog> findByComponent(Component component);
    public List<ErrorLog> findByEnvironment(Environment environment);
}
