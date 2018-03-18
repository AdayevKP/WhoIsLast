package com.whoslast.entities;


import javax.persistence.*;
import java.util.Date;

@Entity
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer queueId;

    private Integer host;

    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

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

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
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
