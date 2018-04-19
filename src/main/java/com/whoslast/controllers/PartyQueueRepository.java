package com.whoslast.controllers;

import com.whoslast.entities.PartyQueue;
import com.whoslast.entities.Queue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyQueueRepository extends CrudRepository<PartyQueue, Long>{

    PartyQueue findByQueue(Queue q);
}
