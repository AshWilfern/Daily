package com.telegram.bot.entity;

import com.telegram.bot.util.Priority;
import com.telegram.bot.util.TaskState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.GregorianCalendar;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task extends com.telegram.bot.entity.DailyEntity {
    @Column(name = "task_name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Column(name = "date", nullable = false)
    private GregorianCalendar date;

    @Column(name = "time", nullable = false)
    private GregorianCalendar time;

    @Column(name = "task_state", nullable = false)
    private TaskState task_state;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;

    public Task(String name, Calendar calendar, String description, GregorianCalendar date, GregorianCalendar time, Priority priority){
        this.name = name;
        this.calendar = calendar;
        this.description = description;
        this.date = date;
        this.time = time;
        this.priority = priority;
        this.task_state = TaskState.NONE;
    }
}
