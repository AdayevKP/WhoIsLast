package com.whoslast.entities;


import javax.persistence.*;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userId;

    private String salt;

    private String hash;

    private Integer hashSize;

    private String name;

    private String email;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="partyId")
    private Party party;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getHashSize(){
        return hashSize;
    }

    public void setHashSize(Integer hashsize){
        this.hashSize = hashsize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Party getPartyId(){
        return party;
    }

    public void setGroupId(Party party){
        this.party = party;
    }

}

