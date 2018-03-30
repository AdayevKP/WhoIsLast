package com.whoslast.controllers;

import com.whoslast.entities.Queue;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueueRepository extends CrudRepository<Queue, Long>{
    @Query(value = "SELECT * FROM queue WHERE queue_id=?1", nativeQuery = true)
    Queue getQueueById(Integer queueId);
}
