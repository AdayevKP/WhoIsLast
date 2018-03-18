package com.whoslast.controllers;

import com.whoslast.entities.QueueRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQueueRepository extends CrudRepository<QueueRecord, Long>{
}
