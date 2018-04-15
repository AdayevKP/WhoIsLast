package com.whoslast.entities;


import javax.persistence.*;

@Entity // This tells Hibernate to make a table out of this class
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer partyId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="superuser_id")
    private Superuser superuser;

    private String name;

    public Integer getPartyId() {
        return partyId;
    }

    public void setSuperuser(Superuser superuser) {
        this.superuser = superuser;
    }

    public Superuser getSuperuser() {
        return superuser;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpeciality() {
        return name;
    }
}
