package com.whoslast.entities;

import javax.persistence.*;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "Invites")
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="userId")
    private User user;


    @ManyToOne
    @JoinColumn(name="partyId")
    private Party party;

    public Integer getId() {
        return id;
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Party getParty() {
        return party;
    }
}