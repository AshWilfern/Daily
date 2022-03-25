package com.services;

import com.entity.Calendar;
import com.repository.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CalendarEntityService implements EntityService<Calendar> {

    private CalendarRepository calendarRepository;

    @Autowired
    public CalendarEntityService(CalendarRepository calendarRepository){
        this.calendarRepository = calendarRepository;
    }

    @Override
    public List<Calendar> getEntities() {
        List<Calendar> result = new ArrayList<>();
        calendarRepository.findAll().forEach(result::add);
        return result;
    }

    @Override
    public Calendar getEntityById(long id) {
        return calendarRepository.findById(id).get();
    }

    @Override
    public void saveEntity(Calendar calendar) {
        calendarRepository.save(calendar);
    }

    @Override
    public void deleteEntity(long id) {
        calendarRepository.deleteById(id);
    }
}
