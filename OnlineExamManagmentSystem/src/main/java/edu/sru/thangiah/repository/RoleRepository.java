package edu.sru.thangiah.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.sru.thangiah.model.Roles;

public interface RoleRepository extends JpaRepository<Roles, Long> {
	
    Optional<Roles> findByName(String name);
    
}