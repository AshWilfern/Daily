package com.telegram.bot.handlers;

import com.telegram.bot.controllers.UserController;
import com.telegram.bot.entity.User;
import com.telegram.bot.util.BotState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.telegram.bot.handlers.AuthHandler.USER_NAME_CHANGE;
import static com.telegram.bot.handlers.CalendarHandler.CALENDAR_CREATE;
import static com.telegram.bot.handlers.CalendarHandler.CALENDAR_MANAGE;
import static com.telegram.bot.handlers.TaskHandler.CHOOSE_CALENDAR;
import static com.telegram.bot.util.Util.*;

@Component
public class HelpHandler implements Handler {
    public static final String HELP = "/help";
    public static final String MAIN = "/main_commands";

    private final UserController userController;

    public HelpHandler (UserController userController) {
        this.userController = userController;
    }

    @Override
    public List<SendMessage> handle(User user, String message) {
        if (message.equalsIgnoreCase(HELP)){
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

            List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                    createInlineKeyboardButton("Change name", USER_NAME_CHANGE));
            List<InlineKeyboardButton> inlineKeyboardButtonsRowTwo = List.of(
                    createInlineKeyboardButton("Create calendar", CALENDAR_CREATE));
            List<InlineKeyboardButton> inlineKeyboardButtonsRowFour = List.of(
                    createInlineKeyboardButton("Manage calendars", CALENDAR_MANAGE));
            List<InlineKeyboardButton> inlineKeyboardButtonsRowFive = List.of(
                    createInlineKeyboardButton("Main commands", MAIN));

            inlineKeyboardMarkup.setKeyboard(List.of(
                    inlineKeyboardButtonsRowOne,
                    inlineKeyboardButtonsRowTwo,
                    inlineKeyboardButtonsRowFour,
                    inlineKeyboardButtonsRowFive));

            SendMessage sendMessage = createMessageTemplate(user);
            sendMessage.setText(String.format("" +
                    "You've asked for help %s? Here it comes!", user.getName()));
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);

            return List.of(sendMessage);
        } else if (message.equalsIgnoreCase(MAIN)){
            SendMessage sendMessage = createMessageTemplate(user);
            sendMessage.setReplyMarkup(createMainKeyboard(user));
            sendMessage.setText("Main commands:");

            return List.of(sendMessage);
        }

        return List.of();
    }
    @Override
    public BotState operatedBotState() {
        return BotState.NONE;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(HELP, MAIN);
    }
}
