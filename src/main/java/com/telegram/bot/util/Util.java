package com.telegram.bot.util;

import com.telegram.bot.entity.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.telegram.bot.handlers.TaskHandler.*;

public class Util {
    public static SendMessage createMessageTemplate(User user) {
        return createMessageTemplate(String.valueOf(user.getChatId()));
    }

    public static SendMessage createMessageTemplate(String chatId) {
        SendMessage res = new SendMessage();
        res.setChatId(chatId);
        res.enableMarkdown(true);
        return res;
    }

    public static InlineKeyboardButton createInlineKeyboardButton(String text, String command) {
        InlineKeyboardButton res = new InlineKeyboardButton();
        res.setText(text);
        res.setCallbackData(command);
        return res;
    }

    public static InlineKeyboardMarkup createMainKeyboard(User user) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Create task", CHOOSE_CALENDAR)
        );
        List<InlineKeyboardButton> inlineKeyboardButtonsRowTwo = List.of(
                createInlineKeyboardButton("Get today tasks", GET_TASKS_BY_DATE)
        );
        List<InlineKeyboardButton> inlineKeyboardButtonsRowThree = List.of(
                createInlineKeyboardButton("Get tasks by day", CHOOSE_DATE_FOR_QUERY)
        );
        List<InlineKeyboardButton> inlineKeyboardButtonsRowFour = List.of(
                createInlineKeyboardButton("Help", "/help")
        );

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne, inlineKeyboardButtonsRowTwo, inlineKeyboardButtonsRowThree, inlineKeyboardButtonsRowFour));

        return inlineKeyboardMarkup;
    }
}
