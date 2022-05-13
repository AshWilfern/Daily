package com.telegram.bot.handlers;

import com.telegram.bot.controllers.CalendarController;
import com.telegram.bot.controllers.UserController;
import com.telegram.bot.entity.User;
import com.telegram.bot.util.BotState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.telegram.bot.handlers.CalendarHandler.CALENDAR_CREATE;
import static com.telegram.bot.util.Util.createInlineKeyboardButton;
import static com.telegram.bot.util.Util.createMessageTemplate;

@Component
public class AuthHandler implements Handler {
    public static final String USER_NAME_ACCEPT = "/name_accept";
    public static final String USER_NAME_CHANGE = "/wait_name";
    public static final String USER_NAME_CHANGE_CANCEL = "/name_change_cancel";

    private final UserController userController;
    private final CalendarController calendarController;

    public AuthHandler(UserController userController, CalendarController calendarController) {
        this.userController = userController;
        this.calendarController = calendarController;
    }

    @Override
    public List<SendMessage> handle(User user, String message) {
        if (message.equalsIgnoreCase(USER_NAME_ACCEPT) || message.equalsIgnoreCase(USER_NAME_CHANGE_CANCEL)) {
            return accept(user);
        } else if (message.equalsIgnoreCase(USER_NAME_CHANGE)) {
            return changeName(user);
        }
        return checkName(user, message);
    }

    private List<SendMessage> accept(User user) {
        // Если пользователь принял имя - меняем статус и сохраняем
        user.setBot_state(BotState.NONE);
        userController.saveUser(user);

        // Создаем кнопку для начала игры
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Create calendar", CALENDAR_CREATE)); //CALENDAR_CREATE

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage message = createMessageTemplate(user);
        message.setText(String.format(
                "Your name is saved as: %s", user.getName()));
        message.setReplyMarkup(inlineKeyboardMarkup);
        return List.of(message);
    }

    private List<SendMessage> checkName(User user, String message) {
        // При проверке имени мы превентивно сохраняем пользователю новое имя в базе
        // идея для рефакторинга - добавить временное хранение имени
        user.setName(message);
        userController.saveUser(user);

        // Делаем кнопку для применения изменений
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Accept", USER_NAME_ACCEPT));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage mes = createMessageTemplate(user);
        mes.setText(String.format("You have entered: %s%nIf this is correct - press the button", user.getName()));
        mes.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(mes);
    }

    private List<SendMessage> changeName(User user) {
        // При запросе изменения имени мы меняем State
        user.setBot_state(BotState.WAIT_USER_NAME);
        userController.saveUser(user);

        // Создаем кнопку для отмены операции
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Cancel", USER_NAME_CHANGE_CANCEL));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage message = createMessageTemplate(user);
        message.setText(String.format(
                "Your current name is: %s%nEnter new name or press the button to continue", user.getName()));
        message.setReplyMarkup(inlineKeyboardMarkup);
        return List.of(message);
    }

    @Override
    public BotState operatedBotState() {
        return BotState.WAIT_USER_NAME;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(USER_NAME_ACCEPT, USER_NAME_CHANGE, USER_NAME_CHANGE_CANCEL);
    }
}
