package com.controllers;

import com.entity.Calendar;
import com.entity.Team;
import com.entity.User;
import com.services.EntityService;

import java.util.List;

public class CalendarController extends EnityController<Calendar> {

    public CalendarController(List<EntityService> entityServices) {
        super(entityServices);
    }

    public void createCalendar(String name, User user){
        Calendar calendar = new Calendar(name, user);
        createCalendar(calendar);
    }

    public void createCalendar(String name, Team team){
        Calendar calendar = new Calendar(name, team);
        createCalendar(calendar);
    }

    private void createCalendar(Calendar calendar)
    {
        getEntityService().saveEntity(calendar);
    }
}
