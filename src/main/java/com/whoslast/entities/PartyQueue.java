package com.whoslast.entities;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity // This tells Hibernate to make a table out of this class
public class PartyQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer recordId;

    private Integer queueId;

    private Integer partyId;

    public Integer getRecordId() {
        return recordId;
    }

    public void setQueueId(Integer queueId) {
        this.queueId = queueId;
    }

    public Integer getQueueId() {
        return queueId;
    }

    public void setPartyId(Integer partyId) {
        this.partyId = partyId;
    }

    public Integer getPartyId() {
        return partyId;
    }
}