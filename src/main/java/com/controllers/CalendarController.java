package com.controllers;

import com.entity.Calendar;
import com.entity.Team;
import com.entity.User;
import com.services.CalendarEntityService;
import com.services.EntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CalendarController {
    private List<EntityService> entityServices;

    @Autowired
    public CalendarController(List<EntityService> entityServices)
    {
        this.entityServices = entityServices;
    }

    public List<Calendar> getCalendars()
    {
        List<Calendar> calendars = new ArrayList<>();
        entityServices.forEach(calendarservice ->calendars.addAll(calendarservice.getEntities()));
        return calendars;
    }

    public Calendar getCalendarById(Long calendarId)
    {
        Calendar calendar = (Calendar) entityServices.stream()
                .map(entityService -> entityService.getEntityById(calendarId))
                .filter(Objects::nonNull)
                .findFirst().orElseGet(null);
        return calendar;
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
        entityServices.stream()
                .filter(calendarService -> calendarService instanceof CalendarEntityService)
                .findFirst()
                .get().saveEntity(calendar);
    }
}
