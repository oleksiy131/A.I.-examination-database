/* Seth Chritzman 																				    */
/*  JpaRepository is a JPA (Java Persistence API) specific extension of Repository. It contains the */
/*  full API of CrudRepository and PagingAndSortingRepository. So it contains API for basic CRUD    */
/*  operations and also API for pagination and sorting. 										    */	
/*  https://www.geeksforgeeks.org/spring-boot-jparepository-with-example/#						    */												



package edu.sru.thangiah.oems.repository;

import edu.sru.thangiah.oems.domain.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    // Add custom query methods here:
}

