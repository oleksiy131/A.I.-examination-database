/* Seth Chritzman 																				    */
/*  JpaRepository is a JPA (Java Persistence API) specific extension of Repository. It contains the */
/*  full API of CrudRepository and PagingAndSortingRepository. So it contains API for basic CRUD    */
/*  operations and also API for pagination and sorting. 										    */	
/*  https://www.geeksforgeeks.org/spring-boot-jparepository-with-example/#						    */												



package edu.sru.thangiah.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.sru.thangiah.domain.Administrator;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    // Add custom query methods here:
	Optional<Administrator> findByAdminUsername(String username);

}

