package com.telegram.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "calendars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Calendar extends com.telegram.bot.entity.DailyEntity {
    @Column(name = "calendar_name", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "calendar")
    private List<com.telegram.bot.entity.Task> tasks;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "user_id", nullable = false)
    private com.telegram.bot.entity.User user;

    public Calendar(String name, com.telegram.bot.entity.User user) {
        this.name = name;
        this.user = user;
    }
}
