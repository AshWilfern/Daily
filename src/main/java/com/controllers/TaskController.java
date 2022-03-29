package com.controllers;

import com.entity.Calendar;
import com.entity.Priority;
import com.entity.Task;
import com.services.EntityService;
import org.springframework.stereotype.Controller;

import java.sql.Date;
import java.util.List;

@Controller
public class TaskController extends EnityController<Task> {

    public TaskController(List<EntityService> entityServices) {
        super(entityServices);
    }

    public void createTask(String name, Calendar calendar, String description, Date start, Date finish, Priority priority)
    {
        Task task = new Task(name,calendar,description,start,finish,priority);
        getEntityService().saveEntity(task);
    }
}
