package com.whoslast.controllers;

import com.whoslast.entities.PartyQueue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupQueueRepository extends CrudRepository<PartyQueue, Long>{
}
