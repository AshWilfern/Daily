package com.telegram.bot.handlers;

import com.telegram.bot.controllers.UserController;
import com.telegram.bot.entity.User;
import com.telegram.bot.util.BotState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Collections;
import java.util.List;

import static com.telegram.bot.util.Util.createMessageTemplate;

@Component
public class StartHandler implements Handler{
    private final UserController userController;

    StartHandler (UserController userController) {
        this.userController = userController;
    }

    @Override
    public List<SendMessage> handle(User user, String message) {
        SendMessage welcomeMessage = createMessageTemplate(user);
        welcomeMessage
                .setText("Hola! I'm your DailyBot \nI am here to help you planning");

        SendMessage registrationMessage = createMessageTemplate(user);
        registrationMessage
                .setText("In order to start our journey tell me your name");
        // Меняем пользователю статус на - "ожидание ввода имени"
        user.setBot_state(BotState.WAIT_USER_NAME);
        userController.saveUser(user);

        return List.of(welcomeMessage, registrationMessage);
    }

    @Override
    public BotState operatedBotState() {
        return BotState.START;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return Collections.emptyList();
    }

}
