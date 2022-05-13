package com.telegram.bot.controllers;

import com.telegram.bot.entity.User;
import com.telegram.bot.services.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {
    @Autowired
    UserEntityService userEntityService;

    public User getUser(Long id) {
        return userEntityService.getEntityById(id);
    }

    public User getUserByChatId(Long chatId) {
        return userEntityService.getUserByChatId(chatId);
    }

    public void saveUser(User user) {
        userEntityService.saveEntity(user);
    }

    public void deleteUser(User user) {
        userEntityService.deleteEntity(user.getId());
    }

    public void createUser(Long chatId) {
        userEntityService.saveEntity(new User(chatId));
    }
}
