package com.telegram.bot.controllers;

import com.telegram.bot.entity.Calendar;
import com.telegram.bot.entity.Task;
import com.telegram.bot.services.CalendarEntityService;
import com.telegram.bot.services.TaskEntityService;
import com.telegram.bot.util.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

@Controller
public class TaskController {
    @Autowired
    TaskEntityService taskEntityService;
    @Autowired
    CalendarEntityService calendarEntityService;

    public Task getTask(Long taskId) {
        return taskEntityService.getEntityById(taskId);
    }

    public Task createTask(String name, Calendar calendar, String description, GregorianCalendar date, GregorianCalendar time, Priority priority){
        Task task = new Task(name, calendar, description, date, time, priority);
        calendar.getTasks().add(task);

        taskEntityService.saveEntity(task);
        calendarEntityService.saveEntity(calendar);

        return task;
    }

    public Optional<List<Task>> getTasksOnDataInUserCalendar(String date, Long calendarId) {
        return taskEntityService.getUserTasksOnDateByCalendarId(date, calendarId);
    }

    public Optional<List<Task>> getTasksInCalendar(Long calendarId) {
        return taskEntityService.findAllByCalendarId(calendarId);
    }

    public void saveTask(Task task) {
        taskEntityService.saveEntity(task);
    }

    public void deleteTask(Task task) {
        task.getCalendar().getTasks().remove(task);
        calendarEntityService.saveEntity(task.getCalendar());
        taskEntityService.deleteEntity(task);
    }


}
