package com.telegram.bot.handlers;

import com.telegram.bot.controllers.CalendarController;
import com.telegram.bot.controllers.UserController;
import com.telegram.bot.entity.Calendar;
import com.telegram.bot.entity.User;
import com.telegram.bot.util.BotState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.telegram.bot.handlers.HelpHandler.HELP;
import static com.telegram.bot.handlers.TaskHandler.CHOOSE_CALENDAR;
import static com.telegram.bot.util.Util.createInlineKeyboardButton;
import static com.telegram.bot.util.Util.createMessageTemplate;

@Component
public class CalendarHandler implements Handler{
    public static final String CALENDAR_CREATE = "/calendar_create";
    public static final String CALENDAR_DELETE = "/calendar_delete";
    public static final String CALENDAR_MANAGE = "/manage_calendars";
    public static final String CALENDAR_NAME_ACCEPT = "/calendar_name_accept";
    public static final String CALENDAR_NAME_CHANGE = "/wait_calendar_name";
    public static final String CALENDAR_NAME_CHANGE_CANCEL = "/calendar_name_change_cancel";

    private final UserController userController;
    private final CalendarController calendarController;

    public CalendarHandler (UserController userController, CalendarController calendarController) {
        this.userController = userController;
        this.calendarController = calendarController;
    }

    @Override
    public List<SendMessage> handle(User user, String message) {
        System.out.println(message);
        String[] quarry = message.split(", ");
        String command = quarry[0];
        System.out.println(command);
//        Long messageId = quarry[1].equals("") ? null : Long.parseLong(quarry[1]);
        Long id;
        Calendar calendar;

        if (user.getCalendars().size() == 0) return createCalendar(user);

        if (quarry.length > 1) {
            id = Long.parseLong(quarry[1]);
            System.out.println(id);
            calendar = calendarController.getCalendar(id);
        } else {
            calendar = user.getCalendars().get(user.getCalendars().size()-1);
        }

        if (command.equalsIgnoreCase(CALENDAR_NAME_ACCEPT) || command.equalsIgnoreCase(CALENDAR_NAME_CHANGE_CANCEL)) {
            return accept(calendar);
        } else if (command.equalsIgnoreCase(CALENDAR_NAME_CHANGE)) {
            return changeCalendarName(calendar);
        } else if (command.equalsIgnoreCase(CALENDAR_CREATE)) {
            return createCalendar(user);
        } else if (command.equalsIgnoreCase(CALENDAR_DELETE)) {
            return deleteCalendar(calendar);
        }else if (command.equalsIgnoreCase(CALENDAR_MANAGE)) {
            return manageCalendars(user);
        }
        return checkCalendarName(calendarController.getCalendar(user.getWork_with()), message);
    }

    public List<SendMessage> createCalendar(User user) {
        calendarController.createCalendar(user);

        SendMessage message = createMessageTemplate(user);
        message.setText("In order to continue our journey lets change the calendar name");
        // Меняем пользователю статус на - "ожидание ввода имени"
        user.setBot_state(BotState.WAIT_CALENDAR_NAME);
        user.setWork_with(user.getCalendars().get(user.getCalendars().size() - 1).getId());
        userController.saveUser(user);

        return List.of(message);
    }

    public List<SendMessage> deleteCalendar(Calendar calendar) {
        if (calendar.getUser().getCalendars().size() == 1) {
            SendMessage message = createMessageTemplate(calendar.getUser());
            message
                    .setText("Sorry, you can't remove your last calendar");
            return List.of(message);
        }

        String name = calendar.getName();
        calendarController.deleteCalendar(calendar);

        SendMessage message = createMessageTemplate(calendar.getUser());
        message
                .setText(String.format("Calendar %s is deleted", name));

        return List.of(message);
    }

    public List<SendMessage> accept(Calendar calendar) {
        calendar.getUser().setBot_state(BotState.NONE);
        userController.saveUser(calendar.getUser());

        SendMessage message = createMessageTemplate(calendar.getUser());

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardRows = new ArrayList<>();

        message.setText(String.format(
                "Calendar name is saved as: %s", calendar.getName()));

        inlineKeyboardRows.add(List.of(createInlineKeyboardButton("Create task", CHOOSE_CALENDAR)));

        calendar.getUser().getCalendars().forEach(x -> {
            List<InlineKeyboardButton> inlineKeyboardButtonsRow = List.of(
                    createInlineKeyboardButton("Rename " + x.getName(), CALENDAR_NAME_CHANGE + ", " + x.getId()),
                    createInlineKeyboardButton("Delete " + x.getName(), CALENDAR_DELETE + ", " + x.getId()));
            inlineKeyboardRows.add(inlineKeyboardButtonsRow);
        });

        inlineKeyboardRows.add(List.of(createInlineKeyboardButton("Create calendar", CALENDAR_CREATE)));
        inlineKeyboardRows.add(List.of(createInlineKeyboardButton("Back", HELP)));

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardRows);

        message.setReplyMarkup(inlineKeyboardMarkup);
        return List.of(message);
    }

    public List<SendMessage> changeCalendarName(Calendar calendar) {
        // При запросе изменения имени мы меняем State
        calendar.getUser().setBot_state(BotState.WAIT_CALENDAR_NAME);
        calendar.getUser().setWork_with(calendar.getId());
        userController.saveUser(calendar.getUser());

        // Создаем кнопку для отмены операции
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Cancel", CALENDAR_NAME_CHANGE_CANCEL + ", " + calendar.getId()));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage message = createMessageTemplate(calendar.getUser());
        message.setText(String.format(
                "Calendar current name is: %s%nEnter new name or press the button to continue", calendar.getName()));
        message.setReplyMarkup(inlineKeyboardMarkup);
        return List.of(message);
    }

    public List<SendMessage> checkCalendarName(Calendar calendar, String message) {
        // При проверке имени мы превентивно сохраняем пользователю новое имя в базе
        // идея для рефакторинга - добавить временное хранение имени
        calendar.setName(message);
        calendarController.saveCalendar(calendar);

        // Делаем кнопку для применения изменений
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Accept", CALENDAR_NAME_ACCEPT + ", " + calendar.getId()));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage mes = createMessageTemplate(calendar.getUser());
        mes.setText(String.format("You have entered: %s%nIf this is correct - press the button", calendar.getName()));
        mes.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(mes);
    }

    public List<SendMessage> manageCalendars(User user) {
        SendMessage message = createMessageTemplate(user);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardRows = new ArrayList<>();

        message.setText("Edit calendars");

        user.getCalendars().forEach(x -> {
            List<InlineKeyboardButton> inlineKeyboardButtonsRow = List.of(
                    createInlineKeyboardButton("Rename " + x.getName(), CALENDAR_NAME_CHANGE + ", " + x.getId()),
                    createInlineKeyboardButton("Delete " + x.getName(), CALENDAR_DELETE + ", " + x.getId()));
            inlineKeyboardRows.add(inlineKeyboardButtonsRow);
        });

        inlineKeyboardRows.add(List.of(createInlineKeyboardButton("Create calendar", CALENDAR_CREATE)));
        inlineKeyboardRows.add(List.of(createInlineKeyboardButton("Back", HELP)));

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardRows);

        message.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(message);
    }

    @Override
    public BotState operatedBotState() {
        return BotState.WAIT_CALENDAR_NAME;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(CALENDAR_CREATE, CALENDAR_DELETE, CALENDAR_NAME_CHANGE, CALENDAR_NAME_ACCEPT, CALENDAR_NAME_CHANGE_CANCEL, CALENDAR_MANAGE);
    }
}
