package com.telegram.bot.handlers;

import com.telegram.bot.entity.User;
import com.telegram.bot.util.BotState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public interface Handler {
    List<SendMessage> handle(User user, String message);
    BotState operatedBotState();
    List<String> operatedCallBackQuery();
}
