package com.telegram.bot.services;

import com.telegram.bot.entity.User;
import com.telegram.bot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserEntityService {
    @Autowired
    private UserRepository userRepository;

    public User getEntityById(long id) {
        return userRepository.getById(id);
    }

    public User getUserByChatId(long chatId) {
        return userRepository.getByChatId(chatId).orElseGet(() -> userRepository.save(new User(chatId)));
    }

    public void saveEntity(User user) {
        userRepository.save(user);
    }

    public void deleteEntity(long id) {
        userRepository.deleteById(id);
    }

//    public void connectCalendarToUser(Calendar calendar, User user) {
//        user.getCalendars().add(calendar);
//        saveEntity(user);
//    }
//
//    public List<Calendar> getCalendars(User user) {
//        return user.getCalendars();
//    }

}
