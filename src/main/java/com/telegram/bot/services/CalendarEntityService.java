package com.telegram.bot.services;

import com.telegram.bot.entity.Calendar;
import com.telegram.bot.entity.Task;
import com.telegram.bot.repository.CalendarRepository;
import com.telegram.bot.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CalendarEntityService {
    @Autowired
    CalendarRepository calendarRepository;
    @Autowired
    TaskRepository taskRepository;

    public Calendar getEntityById(long id) {
        return calendarRepository.getById(id);
    }

    public void saveEntity(Calendar calendar) {
        calendarRepository.save(calendar);
    }

    public void deleteEntity(Calendar calendar) {
        calendar.getTasks().forEach(x ->{
            calendar.getTasks().remove(x);
            taskRepository.delete(x);
        });

        calendarRepository.delete(calendar);
    }

    public  void connectTaskToCalendar(Task task, Calendar calendar) {
        calendar.getTasks().add(task);
        saveEntity(calendar);
    }
}
