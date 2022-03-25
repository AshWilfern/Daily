package com.controllers;

import com.entity.Calendar;
import com.entity.Priority;
import com.entity.Task;
import com.services.EntityService;
import com.services.TaskEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class TaskController {

    private List<EntityService> entityServices;

    @Autowired
    public TaskController(List<EntityService> entityServices)
    {
        this.entityServices = entityServices;
    }

    public List<Task> getTasks()
    {
        List<Task> tasks = new ArrayList<>();
        entityServices.forEach(taskService ->tasks.addAll(taskService.getEntities()));
        return tasks;
    }

    public Task getTasksById(Long taskId)
    {
        Task task = (Task) entityServices.stream()
                .map(entityService -> entityService.getEntityById(taskId))
                .filter(Objects::nonNull)
                .findFirst().orElseGet(null);
        return task;
    }

    public void createTask(String name, Calendar calendar, String description, Date start, Date finish, Priority priority)
    {
        Task task = new Task(name,calendar,description,start,finish,priority);
        entityServices.stream()
                .filter(magicianService -> magicianService instanceof TaskEntityService)
                .findFirst()
                .get().saveEntity(task);
    }
}
