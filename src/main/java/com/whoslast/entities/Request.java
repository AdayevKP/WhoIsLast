package com.whoslast.entities;

import javax.persistence.*;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "Requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer userId;


    @ManyToOne
    @JoinColumn(name="partyId")
    private Party party;

    public Integer getId() {
        return id;
    }

    public void setUserId(Integer userId){
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Party getParty() {
        return party;
    }
}