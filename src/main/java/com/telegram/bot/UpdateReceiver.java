package com.telegram.bot;

import com.telegram.bot.controllers.UserController;
import com.telegram.bot.entity.User;
import com.telegram.bot.handlers.Handler;
import com.telegram.bot.util.BotState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

@Component
public class UpdateReceiver {
    private final List<Handler> handlers;
    private final UserController userController;

    public UpdateReceiver(List<Handler> handlers, UserController userController) {
        this.handlers = handlers;
        this.userController = userController;
    }

    public List<SendMessage> handle(Update update){
        EditMessageMedia editMessageMedia = new EditMessageMedia();
        try {
            if (isMessageWithText(update)) {
                final Message message = update.getMessage();
                final long chatId = message.getFrom().getId();

                final User user = userController.getUserByChatId(chatId);
                // Ищем нужный обработчик и возвращаем результат его работы
                return getHandlerByState(user.getBot_state()).handle(user, message.getText());

            } else if (update.hasCallbackQuery()) {
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final long chatId = callbackQuery.getFrom().getId();
                final User user = userController.getUserByChatId(chatId);

                return getHandlerByCallBackQuery(callbackQuery.getData()).handle(user, callbackQuery.getData());
            }

            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException e) {
            return Collections.emptyList();
        }
    }

    private Handler getHandlerByState(BotState state) {
        return handlers.stream()
                .filter(h -> h.operatedBotState() != null)
                .filter(h -> h.operatedBotState().equals(state))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private Handler getHandlerByCallBackQuery(String query) {
        return handlers.stream()
                .filter(h -> h.operatedCallBackQuery().stream()
                        .anyMatch(query::startsWith))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }
}
