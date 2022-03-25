package com.example.demo.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class DairyBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String name;

    @Value("${bot.token}")
    private String token;

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();

            String userId = update.getMessage().getChatId().toString();

            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow row = new KeyboardRow();
            row.add("/start");
            keyboard.add(row);
            keyboardMarkup.setKeyboard(keyboard);
            message.setReplyMarkup(keyboardMarkup);

            if (update.getMessage().getText().equals("/start")) {
                String text = "Hi!\nNow you can start planning!";

                message.setChatId(userId);
                message.setText(text);

                keyboardMarkup = new ReplyKeyboardMarkup();
                keyboard = new ArrayList<>();
                row = new KeyboardRow();
                row.add("/stop");
                keyboard.add(row);
                keyboardMarkup.setKeyboard(keyboard);

                message.setReplyMarkup(keyboardMarkup);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else if (update.getMessage().getText().equals("/stop")) {
                String text = "Bye...\nHope we'll meet again!\n\uD83D\uDE22";

                message.setChatId(userId);
                message.setText(text);

                keyboardMarkup = new ReplyKeyboardMarkup();
                keyboard = new ArrayList<>();
                row = new KeyboardRow();
                row.add("/start");
                keyboard.add(row);
                keyboardMarkup.setKeyboard(keyboard);

                message.setReplyMarkup(keyboardMarkup);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (update.getMessage().getText().startsWith("/")) message.setText("Неизвестная команда");
        }
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
