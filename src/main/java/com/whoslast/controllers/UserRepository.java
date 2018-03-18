package com.whoslast.controllers;

import com.whoslast.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// Auto-implemented by Spring
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    //example of custom sql query
    @Query(value = "SELECT * FROM users WHERE name LIKE ?1", nativeQuery = true)
    User findUserByNameStartingWith(String name);

    //Get user tuple by email
    @Query(value = "SELECT * FROM users WHERE email=?1", nativeQuery = true)
    User findUserByEmail(String email);
}
