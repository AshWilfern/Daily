package com.entity;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "team")
public class Team implements DailyEntity {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name= "increment", strategy= "increment")
    @Column(name = "id", length = 6, nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "team")
    private List<User> users;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "team")
    private List<Calendar> calendars;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return null;
    }

    public List<User> getUsers() {
        return users;
    }
}
