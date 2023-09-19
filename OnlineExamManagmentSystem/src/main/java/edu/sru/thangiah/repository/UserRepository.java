package edu.sru.thangiah.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import edu.sru.thangiah.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    // Add custom query methods here:
	@SuppressWarnings("unchecked")
	User save(User user);
	
	Optional<User> findByUsername(String username);

}
