package com.whoslast.controllers;

import com.whoslast.entities.PartyQueue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyQueueRepository extends CrudRepository<PartyQueue, Long>{
}
