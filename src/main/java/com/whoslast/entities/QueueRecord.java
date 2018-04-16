package com.whoslast.entities;


import javax.persistence.*;


@Entity
@Table(name="List")// This tells Hibernate to make a table out of this class
public class QueueRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer recordId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="queueId")
    private Queue queue;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="userId")
    private User user;

    private Integer number;

    public Integer getRecordId() {
        return recordId;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }
}