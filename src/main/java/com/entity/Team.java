package com.entity;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name= "increment", strategy= "increment")
    @Column(name = "id", length = 6, nullable = false)
    private long id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "team")
    private List<User> users;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "team")
    private List<Calendar> calendars;

    public long getId() {
        return id;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
