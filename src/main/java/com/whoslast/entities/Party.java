package com.whoslast.entities;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer partyId;

    private Integer superuser;

    private String speciality;

    public Integer getPartyId() {
        return partyId;
    }

    public void setSuperuser(Integer superuser) {
        this.superuser = superuser;
    }

    public Integer getSuperuser() {
        return superuser;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getSpeciality() {
        return speciality;
    }
}
