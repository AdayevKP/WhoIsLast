package com.whoslast.controllers;

import com.whoslast.entities.Superuser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// Auto-implemented by Spring
@Repository
public interface SuperuserRepository extends CrudRepository<Superuser, Long> {

        Superuser findByUserId(Integer id);
}