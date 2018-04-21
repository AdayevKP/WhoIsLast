package com.whoslast.entities;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;


@Entity
@Table(name="List", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "queueId"}))// This tells Hibernate to make a table out of this class
public class QueueRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer recordId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="queueId")
    @OnDelete(action =  OnDeleteAction.CASCADE)
    private Queue queue;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="userId")
    @OnDelete(action =  OnDeleteAction.CASCADE)
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