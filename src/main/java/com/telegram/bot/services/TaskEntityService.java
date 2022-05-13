package com.telegram.bot.services;

import com.telegram.bot.entity.Task;
import com.telegram.bot.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskEntityService {
    @Autowired
    TaskRepository taskRepository;

    public Task getEntityById(long id) {
        return taskRepository.getById(id);
    }

    public void saveEntity(Task task) {
        taskRepository.save(task);
    }

    public void deleteEntity(Task task) {
        taskRepository.delete(task);;
    }

    public Optional<List<Task>> getUserTasksOnDateByCalendarId(String date, long calendarId) {
        return taskRepository.getTasksOnDateByCalendarId(new SimpleDateFormat(date).parse("yyyy-MM-dd", new ParsePosition(0)), calendarId);
    }

    public Optional<List<Task>> findAllByCalendarId(long calendarId) {
        return taskRepository.findAllByCalendarId(calendarId);
    }
}
