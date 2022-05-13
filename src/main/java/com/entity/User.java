package com.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "id", length = 6, nullable = false)
    private long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "firstname", nullable = true)
    private String firstName;

    @Column(name = "surname", nullable = true)
    private String surname;

    @Column(name = "email", nullable = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "team_id", nullable = true)
    private Team team;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<Calendar> calendars;

    public long getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public List<Calendar> getCalendars() {
        return calendars;
    }
}
