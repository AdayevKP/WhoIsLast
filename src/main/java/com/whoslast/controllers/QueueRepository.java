package com.whoslast.controllers;

import com.whoslast.entities.Queue;
import com.whoslast.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface QueueRepository extends CrudRepository<Queue, Long>{
    @Query(value = "SELECT * FROM queue WHERE queue_id=?1", nativeQuery = true)
    Queue getQueueById(Integer queueId);

    @Query(value = "SELECT * FROM queue WHERE queue_id IN (SELECT queue_id FROM party_queue WHERE party_id=?1)", nativeQuery = true)
    Iterable<Queue> getQueuesEntriesByPartyId(Integer partyId);

    @Query(value = "SELECT * FROM queue WHERE queue_id IN (SELECT queue_id FROM party_queue WHERE party_id=?1) AND NOT queue_id IN (SELECT queue_id FROM list WHERE user_id=?2)", nativeQuery = true)
    Iterable<Queue> getQueuesEntriesAvailableToUser(Integer partyId, Integer userId);

    @Query(value = "SELECT * FROM queue WHERE queue_id IN (SELECT queue_id FROM party_queue WHERE party_id=?1) AND queue_id IN (SELECT queue_id FROM list WHERE user_id=?2)", nativeQuery = true)
    Iterable<Queue> getQueuesEntriesUserAlreadyIn(Integer partyId, Integer userId);

    @Query(value = "SELECT * FROM queue WHERE (time=?1) AND (place=?2)", nativeQuery = true)
    Queue getQueueByTimeAndPlace(Date time, String place);

    @Query(value = "SELECT * FROM queue WHERE (professor=?1) AND (place=?2)", nativeQuery = true)
    Queue getQueueByProfAndPlace(String prof, String place);

    @Query(value = "SELECT user_id FROM users WHERE user_id IN (SELECT user_id FROM list WHERE queue_id=?1)", nativeQuery = true)
    List<Integer> findAllUsersInQueue(Integer queueId);

    Iterable<Queue> findByQueueName(String name);
}
