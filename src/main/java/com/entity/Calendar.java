package com.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "calendar")
public class Calendar implements DailyEntity {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name= "increment", strategy= "increment")
    @Column(name = "id", length = 6, nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "calendar")
    private List<Task> tasks;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "team_id", nullable = true)
    private Team team;

    public Calendar(String name, User user) {
        this(name);
        this.user = user;
    }

    public Calendar(String name, Team team) {
        this(name);
        this.team = team;
    }

    private Calendar(String name){
        this.name = name;
        tasks = new ArrayList<>();
    }

    public Calendar(){}

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Team getTeam() {
        return team;
    }

    public User getUser() {
        return user;
    }
}
