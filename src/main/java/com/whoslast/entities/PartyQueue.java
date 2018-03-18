package com.whoslast.entities;


import javax.persistence.*;


@Entity // This tells Hibernate to make a table out of this class
public class PartyQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer recordId;

    @ManyToOne
    @JoinColumn(name="queueId")
    private Queue queue;

    @ManyToOne
    @JoinColumn(name="partyId")
    private Party party;

    public Integer getRecordId() {
        return recordId;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public Queue getQueueId() {
        return queue;
    }

    public void setPartyId(Party party) {
        this.party = party;
    }

    public Party getPartyId() {
        return party;
    }
}