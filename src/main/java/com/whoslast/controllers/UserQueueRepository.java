package com.whoslast.controllers;

import com.whoslast.entities.Queue;
import com.whoslast.entities.QueueRecord;
import com.whoslast.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQueueRepository extends CrudRepository<QueueRecord, Long>{
    @Query(value = "SELECT number FROM list WHERE number = (SELECT MAX(number) from (SELECT * FROM list WHERE queue_id=?1) AS T)", nativeQuery = true)
    Integer getLastNumberInCurrentQueue(Integer queueId);

    @Query(value = "SELECT EXISTS(SELECT * FROM list WHERE user_id=?1 AND queue_id=?2)", nativeQuery = true)
    Integer checkExistence(Integer userId, Integer queueId);

    QueueRecord findByQueueAndUser(Queue queue,User user);
}
