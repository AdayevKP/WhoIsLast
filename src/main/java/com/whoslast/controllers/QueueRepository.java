package com.whoslast.controllers;

import com.whoslast.entities.Queue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueueRepository extends CrudRepository<Queue, Long>{
}
