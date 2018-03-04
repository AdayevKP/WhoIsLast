package com.whoslast.controllers;

import com.whoslast.entities.UserQueue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQueueRepository extends CrudRepository<UserQueue, Long>{
}
