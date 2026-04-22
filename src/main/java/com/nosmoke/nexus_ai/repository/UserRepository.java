package com.nosmoke.nexus_ai.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nosmoke.nexus_ai.model.User;
import com.nosmoke.nexus_ai.model.User.Role;



@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    public Optional<User> findByUsername(String username);

    public Optional<User> findByRole(Role role);


    
}
