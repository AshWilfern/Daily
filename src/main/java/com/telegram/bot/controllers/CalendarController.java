package com.telegram.bot.controllers;

import com.telegram.bot.entity.Calendar;
import com.telegram.bot.entity.User;
import com.telegram.bot.services.CalendarEntityService;
import com.telegram.bot.services.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CalendarController {
    @Autowired
    CalendarEntityService calendarEntityService;
    @Autowired
    UserEntityService userEntityService;

    public Calendar getCalendar(Long calendar_id) {
        return calendarEntityService.getEntityById(calendar_id);
    }

    public void createCalendar(User user){
        Calendar calendar = new Calendar("new", user);
        user.getCalendars().add(calendar);

        calendarEntityService.saveEntity(calendar);
        userEntityService.saveEntity(user);
    }

    public void saveCalendar(Calendar calendar) {
        calendarEntityService.saveEntity(calendar);
    }

    public void deleteCalendar(Calendar calendar) {
        calendarEntityService.deleteEntity(calendar);
    }
}
