package edu.sru.thangiah.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.sru.thangiah.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Add custom query methods here:
	@SuppressWarnings("unchecked")
	User save(User user);

}
