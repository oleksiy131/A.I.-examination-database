package edu.sru.thangiah.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.ScheduleManager;



public interface ScheduleManagerRepository extends JpaRepository<ScheduleManager, Long> {
    Optional<ScheduleManager> findBymanagerUsername(String username);  // Updated method name to match field name

	List<ScheduleManager> findByManagerFirstNameContaining(String searchParam);

	List<ScheduleManager> findByManagerUsernameContaining(String searchParam);
}