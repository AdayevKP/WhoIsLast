package com.whoslast.entities;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity // This tells Hibernate to make a table out of this class
public class UserQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer recordId;

    private Integer queueId;

    private Integer userId;

    private Integer ord;

    public Integer getRecordId() {
        return recordId;
    }

    public void setQueueId(Integer queueId) {
        this.queueId = queueId;
    }

    public Integer getQueueId() {
        return queueId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setOrd(Integer ord) {
        this.ord = ord;
    }

    public Integer getOrd() {
        return ord;
    }
}