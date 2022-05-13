package com.telegram.bot.repository;

import com.telegram.bot.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query(value = "SELECT * FROM tasks as t WHERE t.date = :date AND t.calendar_id = :id ORDER by t.time", nativeQuery = true)
    Optional<List<Task>> getTasksOnDateByCalendarId(@Param("date") @Temporal(TemporalType.DATE) Date date, @Param("id") Long calendar_id);

    @Query(value = "SELECT * FROM tasks as t WHERE t.calendar_id = :id ORDER by t.time", nativeQuery = true)
    Optional<List<Task>> findAllByCalendarId(@Param("id") Long calendarId);
}
