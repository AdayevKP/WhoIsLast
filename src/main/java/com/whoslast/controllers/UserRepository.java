package com.whoslast.controllers;

import com.whoslast.entities.Party;
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
    @Query(value = "SELECT * FROM users WHERE email=?1 AND registration_code IS NULL", nativeQuery = true)
    User findUserByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE email=?1", nativeQuery = true)
    User findUnverifiedUserByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE user_id=?1 AND registration_code IS NULL", nativeQuery = true)
    Iterable<User> findUserById(Integer id);

    @Query(value = "SELECT * FROM users WHERE party_id=?1 AND NOT user_id=?2 AND registration_code IS NULL", nativeQuery = true)
    Iterable<User> findGroupMates(Party partyId, Integer currentUserId);
}
