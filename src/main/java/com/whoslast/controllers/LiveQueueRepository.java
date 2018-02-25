package com.whoslast.controllers;

import com.whoslast.entities.LiveQueue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiveQueueRepository extends CrudRepository<LiveQueue, Long>{
}
