package com.whoslast.entities;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer queueId;

    private Integer host;

    private String time;

    private String place;

    private String professor;

    public Integer getQueueId() {
        return queueId;
    }

    public void setHost(Integer host){
        this.host = host;
    }

    public Integer getHost() {
        return host;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getProfessor() {
        return professor;
    }
}
