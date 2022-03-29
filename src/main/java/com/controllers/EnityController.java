package com.controllers;

import com.entity.Calendar;
import com.entity.DailyEntity;
import com.entity.Priority;
import com.entity.Task;
import com.services.EntityService;
import com.services.TaskEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class EnityController<T extends DailyEntity> {
    private List<EntityService> entityServices;

    @Autowired
    public EnityController(List<EntityService> entityServices)
    {
        this.entityServices = entityServices;
    }

    public List<T> getTasks()
    {
        List<T> entities = new ArrayList<>();
        entityServices.forEach(taskService ->entities.addAll(taskService.getEntities()));
        return entities;
    }

    public T getTasksById(Long entityId)
    {
        T entity = (T) entityServices.stream()
                .map(entityService -> entityService.getEntityById(entityId))
                .filter(Objects::nonNull)
                .findFirst().orElseGet(null);
        return entity;
    }

    /*public void createEntity(String name, Calendar calendar, String description, Date start, Date finish, Priority priority)
    {
        T task = new Task(name,calendar,description,start,finish,priority);
        getEntityService().saveEntity(task);
    }*/

    public <S> void updateEntity(long id, String fieldName, S value) throws NoSuchFieldException, IllegalAccessException {
        T entity = getTasksById(id);
        Class entityClass = entity.getClass();
        entityClass.getField(fieldName).set(entityClass, value);
        getEntityService().saveEntity(entity);
    }

    public void deleteEntity(long id){
        getEntityService().deleteEntity(id);
    }

    protected EntityService getEntityService() {
        return entityServices.stream()
                .filter(magicianService -> magicianService instanceof TaskEntityService)
                .findFirst()
                .get();
    }
}
