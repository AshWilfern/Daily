package com.entity;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name= "increment", strategy= "increment")
    @Column(name = "id", length = 6, nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "finish_date", nullable = true)
    private Date finishDate;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }
}
