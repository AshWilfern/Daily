package com.telegram.bot.handlers;

import com.telegram.bot.controllers.CalendarController;
import com.telegram.bot.controllers.TaskController;
import com.telegram.bot.controllers.UserController;
import com.telegram.bot.entity.Calendar;
import com.telegram.bot.entity.Task;
import com.telegram.bot.entity.User;
import com.telegram.bot.util.BotState;
import com.telegram.bot.util.Priority;
import com.telegram.bot.util.TaskState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static com.telegram.bot.util.Util.*;

@Component
public class TaskHandler implements Handler {
    public static final String CHOOSE_CALENDAR = "/choose_calendar";
    public static final String CREATE_TASK = "/create_task";
    public static final String TASK_DELETE = "/task_delete";
    public static final String CHOOSE_DATE = "/choose_task_date";
    public static final String SET_TASK_DATE = "/set_task_date";
    public static final String CHOOSE_TASK_TIME = "/choose_task_time";
    public static final String SET_TASK_TIME = "/set_task_time";
    public static final String CHOOSE_PRIORITY = "/choose_task_priority";
    public static final String SET_PRIORITY = "/set_task_priority";
    public static final String TASK_ACCEPT = "/enter_task_accept";
    public static final String TASK_NAME_CHANGE = "/enter_task_name";
    public static final String TASK_EDIT = "/edit_task";

    public static final String GET_TASKS_BY_DATE = "/get_tasks_by_date";
    public static final String CHOOSE_DATE_FOR_QUERY = "/enter_date_for_query";
    public static final String SET_TASK_STATE = "/set_task_state";

    private final UserController userController;
    private final CalendarController calendarController;
    private final TaskController taskController;

    public TaskHandler (UserController userController, CalendarController calendarController, TaskController taskController) {
        this.userController = userController;
        this.calendarController = calendarController;
        this.taskController = taskController;
    }

    @Override
    public List<SendMessage> handle(User user, String message) {
        if (message.startsWith(CHOOSE_CALENDAR)) {
            return chooseCalendar(user);
        } else if (message.startsWith(CREATE_TASK)) {
            return createTask(user, message);
        } else if (message.startsWith(GET_TASKS_BY_DATE)) {
            return getTasksOnDate(user, message);
        } else if (message.startsWith(CHOOSE_DATE_FOR_QUERY)) {
            return chooseDateForQuery(user, message);
        }

        String[] quarry = message.split("&=");
        String command = quarry[0];
        Long id;
        if (quarry.length < 2) {
            id = user.getWork_with();
        } else id = Long.parseLong(quarry[1]);
        Task task = taskController.getTask(id);

        if (message.startsWith(TASK_NAME_CHANGE)){
            return changeTaskName(task);
        } else if (message.startsWith(CHOOSE_PRIORITY)) {
            return chooseTaskPriority(task);
        } else if (message.startsWith(SET_PRIORITY)) {
            return setTaskPriority(task, quarry[2]);
        } else if (message.startsWith(CHOOSE_TASK_TIME)) {
            return chooseTaskTime(task, message);
        } else if (message.startsWith(SET_TASK_TIME)) {
            return setTaskTime(task, quarry[2]);
        } else if (message.startsWith(CHOOSE_DATE)) {
            return chooseTaskDate(task, message);
        } else if (message.startsWith(SET_TASK_DATE)) {
            return setTaskDate(task, quarry[2]);
        } else if (message.startsWith(TASK_DELETE)) {
            return deleteTask(task);
        } else if (message.startsWith(TASK_ACCEPT)) {
            return accept(task);
        } else if (message.startsWith(TASK_EDIT)) {
            return edit(task);
        } else if (message.startsWith(SET_TASK_STATE)) {
            return changeTaskState(task);
        }
        return setName(task, message);
    }

    public List<SendMessage> chooseCalendar(User user) {
        SendMessage message;

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardRows = new ArrayList<>();

        user.getCalendars().forEach(x -> {
            List<InlineKeyboardButton> inlineKeyboardButtonsRow = List.of(
                    createInlineKeyboardButton(x.getName(), CREATE_TASK + ", " + x.getId()));
            inlineKeyboardRows.add(inlineKeyboardButtonsRow);
        });

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardRows);

        message = createMessageTemplate(user);
        message.setText("Choose calendar, where you want to create task:");
        message.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(message);
    }

    public List<SendMessage> createTask(User user, String message) {
        String[] query = message.split(", ");
        Calendar calendar = calendarController.getCalendar(Long.parseLong(query[1]));
        GregorianCalendar date = new GregorianCalendar();
        date.set(java.util.Calendar.HOUR, 0);
        date.set(java.util.Calendar.MINUTE, 0);
        date.set(java.util.Calendar.SECOND, 0);
        date.set(java.util.Calendar.MILLISECOND, 0);

        Task task = taskController.createTask("new", calendar, "", date, date, Priority.LOW);

        user.setWork_with(calendarController.getCalendar(Long.parseLong(query[1])).getTasks()
                .get(calendarController.getCalendar(Long.parseLong(query[1])).getTasks().size() - 1).getId());
        userController.saveUser(user);

        SendMessage result = displayTask(taskController.getTask(user.getWork_with()));

        result.setReplyMarkup(createChangeTaskInlineKeyboard(taskController.getTask(user.getWork_with()).getId()));

        return List.of(result);
    }

    public SendMessage displayTask(Task task) {
        SendMessage message;
        String priority = "\uD83D\uDFE9";
        String state = "ToSoon";
        String name = task.getName();
        String description = task.getDescription();

        GregorianCalendar calendar = task.getDate();
        SimpleDateFormat formater = new SimpleDateFormat("dd MMM y");
        String date = formater.format(calendar.getTime());
        calendar = task.getTime();
        formater = new SimpleDateFormat("HH:mm");
        String time = formater.format(calendar.getTime());

        switch (task.getPriority()) {
            case LOW:
                priority = "\uD83D\uDFE9";
                break;
            case MIDDLE:
                priority = "\uD83D\uDFE8";
                break;
            case HIGH:
                priority = "\uD83D\uDFE5";
                break;
        }

        switch (task.getTask_state()) {
            case NONE:
                state = "ToSoon";
                break;
            case INPROGRESS:
                state = "InProgress";
                break;
            case DONE: {
                state = "Done";
                priority = "✅";
                break;
            }
        }

        message = createMessageTemplate(task.getCalendar().getUser());
        String text = priority + " " + name + "\n" + time + " " + date + "\n" + state + "\n" + description;
        message.setText(text);

        return message;
    }

    public InlineKeyboardMarkup createChangeTaskInlineKeyboard(Long taskId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
//                createInlineKeyboardButton("Add description", ENTER_TASK_DESCRIPTION + ", " + taskRepository.getById(user.getWork_with())),
                createInlineKeyboardButton("Set task", TASK_NAME_CHANGE + "&=" + taskId),
                createInlineKeyboardButton("Change priority", CHOOSE_PRIORITY + "&=" + taskId),
                createInlineKeyboardButton("Change date", CHOOSE_DATE + "&=" + taskId),
                createInlineKeyboardButton("Change time", CHOOSE_TASK_TIME + "&=" + taskId + "&=" + 0)
        );
        List<InlineKeyboardButton> inlineKeyboardButtonsRowTwo = List.of(
                createInlineKeyboardButton("Accept", TASK_ACCEPT + "&=" + taskId),
                createInlineKeyboardButton("Delete", TASK_DELETE + "&=" + taskId)
        );

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne, inlineKeyboardButtonsRowTwo));

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup createAboutTaskInlineKeyboard(Long taskId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton(taskController.getTask(taskId).getTask_state() == TaskState.NONE ? "InProgress" : "Done",
                        SET_TASK_STATE + "&=" + taskId),
                createInlineKeyboardButton("Edit", TASK_EDIT + "&=" + taskId)
        );

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        return inlineKeyboardMarkup;
    }

    public List<SendMessage> changeTaskName(Task task) {
        task.getCalendar().getUser().setBot_state(BotState.WAIT_TASK_NAME);
        task.getCalendar().getUser().setWork_with(task.getId());
        userController.saveUser(task.getCalendar().getUser());

        SendMessage message = createMessageTemplate(task.getCalendar().getUser());
        message
                .setText("Enter task:");

        return List.of(message);
    }

    public List<SendMessage> setName(Task task, String message) {
        task.setName(message);
        taskController.saveTask(task);

        task.getCalendar().getUser().setBot_state(BotState.NONE);
        userController.saveUser(task.getCalendar().getUser());

        SendMessage result = displayTask(task);

        result.setReplyMarkup(createChangeTaskInlineKeyboard(task.getId()));

        return List.of(result);
    }

    public List<SendMessage> chooseTaskPriority(Task task) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardRawOne = List.of(
                createInlineKeyboardButton("Low", SET_PRIORITY + "&=" + task.getId()+ "&=" + 0));
        List<InlineKeyboardButton> inlineKeyboardRawTwo = List.of(
                createInlineKeyboardButton("Middle", SET_PRIORITY + "&=" + task.getId()+ "&=" + 1));
        List<InlineKeyboardButton> inlineKeyboardRawThree = List.of(
                createInlineKeyboardButton("High", SET_PRIORITY + "&=" + task.getId()+ "&=" + 2));
        List<InlineKeyboardButton> inlineKeyboardRawFour = List.of(
                createInlineKeyboardButton("Cancel", TASK_EDIT + "&=" + task.getId()));

        inlineKeyboardMarkup.setKeyboard(List.of(
                inlineKeyboardRawOne,
                inlineKeyboardRawTwo,
                inlineKeyboardRawThree,
                inlineKeyboardRawFour
        ));

        SendMessage sendMessage = createMessageTemplate(task.getCalendar().getUser());
        sendMessage.setText("Select priority");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }

    public List<SendMessage> setTaskPriority(Task task, String message) {
        switch (Integer.parseInt(message)) {
            case 0:
                task.setPriority(Priority.LOW);
                break;
            case 1:
                task.setPriority(Priority.MIDDLE);
                break;
            case 2:
                task.setPriority(Priority.HIGH);
                break;
        }
        taskController.saveTask(task);

        SendMessage result = displayTask(task);

        result.setReplyMarkup(createChangeTaskInlineKeyboard(task.getId()));

        return List.of(result);
    }

    public List<SendMessage> chooseTaskDate(Task task, String message) {
        SendMessage result;
        String[] query = message.split("&=");
        InlineKeyboardMarkup inlineKeyboardMarkup;

        if (query.length < 3) {
            inlineKeyboardMarkup = createCalendarKeyboard(
                    task.getDate().get(java.util.Calendar.MONTH),
                    task.getDate().get(java.util.Calendar.YEAR),
                    task.getId(),
                    CHOOSE_DATE,
                    SET_TASK_DATE);
        } else {
            String[] dateParts = query[2].split("-");
            inlineKeyboardMarkup = createCalendarKeyboard(
                    Integer.parseInt(dateParts[0]),
                    Integer.parseInt(dateParts[1]),
                    task.getId(),
                    CHOOSE_DATE,
                    SET_TASK_DATE);
        }

        result = createMessageTemplate(task.getCalendar().getUser());
        result.setText("Choose date:");
        result.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(result);
    }

    public InlineKeyboardMarkup createCalendarKeyboard(Integer month, Integer year, Long taskId, String command1, String command2) {
        GregorianCalendar currentMonth = new GregorianCalendar();

        currentMonth.set(java.util.Calendar.DATE, 1);
        currentMonth.set(java.util.Calendar.MONTH, month);
        currentMonth.set(java.util.Calendar.YEAR, year);

        Integer weekDay = currentMonth.get(java.util.Calendar.DAY_OF_WEEK);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboard = new ArrayList<>();

        List<InlineKeyboardButton> inlineKeyboardButtonsRow = new ArrayList<>();
        currentMonth.add(java.util.Calendar.MONTH,-1);
        inlineKeyboardButtonsRow.add(createInlineKeyboardButton("<", command1 + "&=" + taskId + "&=" +
                currentMonth.get(java.util.Calendar.MONTH) + "-" + currentMonth.get(java.util.Calendar.YEAR)));
        inlineKeyboardButtonsRow.add(createInlineKeyboardButton(String.format("%s", month + 1), "ThisIsUnuseful"));
        inlineKeyboardButtonsRow.add(createInlineKeyboardButton(String.format("%s", year), "ThisIsUnuseful"));
        currentMonth.add(java.util.Calendar.MONTH,2);
        inlineKeyboardButtonsRow.add(createInlineKeyboardButton(">", command1 + "&=" + taskId + "&=" +
                currentMonth.get(java.util.Calendar.MONTH) + "-" + currentMonth.get(java.util.Calendar.YEAR)));

        currentMonth.add(java.util.Calendar.MONTH,-1);

        inlineKeyboard.add(inlineKeyboardButtonsRow);
        inlineKeyboardButtonsRow = List.of(
                createInlineKeyboardButton("ВС", "ThisIsUnuseful"),
                createInlineKeyboardButton("ПН", "ThisIsUnuseful"),
                createInlineKeyboardButton("ВТ", "ThisIsUnuseful"),
                createInlineKeyboardButton("СР", "ThisIsUnuseful"),
                createInlineKeyboardButton("ЧТ", "ThisIsUnuseful"),
                createInlineKeyboardButton("ПТ", "ThisIsUnuseful"),
                createInlineKeyboardButton("СБ", "ThisIsUnuseful")
        );
        inlineKeyboard.add(inlineKeyboardButtonsRow);
        inlineKeyboardButtonsRow = new ArrayList();

        int i = 1;
        while (i < weekDay) {
            inlineKeyboardButtonsRow.add(createInlineKeyboardButton("*", "ThisIsUnuseful"));
            i += 1;
        }

        while (currentMonth.get(java.util.Calendar.MONTH) == (month)) {
            for (; weekDay <= 7; weekDay++) {
                String date = currentMonth.get(java.util.Calendar.DATE) + "-" +
                        (currentMonth.get(java.util.Calendar.MONTH)) + "-" +
                        currentMonth.get(java.util.Calendar.YEAR);

                inlineKeyboardButtonsRow.add(createInlineKeyboardButton(
                        String.valueOf(
                                currentMonth.get(java.util.Calendar.DATE)),
                        command2 + "&=" + taskId + "&=" + date));

                currentMonth.add(java.util.Calendar.DATE,1);

                if (currentMonth.get(java.util.Calendar.MONTH) != (month)) {
                    while (weekDay < 7){
                        inlineKeyboardButtonsRow.add(createInlineKeyboardButton("*", "ThisIsUnuseful"));
                        weekDay++;
                    }
                }
            }
            inlineKeyboard.add(inlineKeyboardButtonsRow);
            inlineKeyboardButtonsRow = new ArrayList<>();

            weekDay = 1;
        }

        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);

        return inlineKeyboardMarkup;
    }

    public List<SendMessage> setTaskDate(Task task, String message) {
        String[] dateParts = message.split("-");
        GregorianCalendar date = new GregorianCalendar(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[0]));
        task.setDate(date);
        taskController.saveTask(task);

        task.getCalendar().getUser().setBot_state(BotState.NONE);
        userController.saveUser(task.getCalendar().getUser());

        SendMessage result = displayTask(task);

        result.setReplyMarkup(createChangeTaskInlineKeyboard(task.getId()));

        return List.of(result);
    }

    public List<SendMessage> chooseTaskTime(Task task, String message) {
        SendMessage sendMessage = createMessageTemplate(task.getCalendar().getUser());
        sendMessage.setText("Select time:");
        sendMessage.setReplyMarkup(createTimeKeyboard(Integer.parseInt(message.split("&=")[2]), task.getId()));

        return List.of(sendMessage);
    }

    public InlineKeyboardMarkup createTimeKeyboard(Integer hour, Long taskId) {
        GregorianCalendar currentTime = new GregorianCalendar();
        currentTime.set(java.util.Calendar.HOUR,hour);
        currentTime.set(java.util.Calendar.MINUTE,0);
        currentTime.set(java.util.Calendar.SECOND,0);
        currentTime.set(java.util.Calendar.MILLISECOND,0);

        String time = "";

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> inlineKeyboardRow = new ArrayList<>();
        List<List<InlineKeyboardButton>> inlineKeyboard = new ArrayList<>();

        int max = 0;
        switch (hour) {
            case 0:
                max = 8;
                inlineKeyboardRow.add(createInlineKeyboardButton("0:00 - 7.45", "Unuseful now"));
                inlineKeyboardRow.add(createInlineKeyboardButton("8:00 - 15:45", CHOOSE_TASK_TIME + "&=" + taskId + "&=" + 8));
                inlineKeyboardRow.add(createInlineKeyboardButton("16:00 - 23:45", CHOOSE_TASK_TIME + "&=" + taskId + "&=" + 16));
                break;
            case 8:
                max = 16;
                inlineKeyboardRow.add(createInlineKeyboardButton("0:00 - 7.45", CHOOSE_TASK_TIME + "&=" + taskId + "&=" + 0));
                inlineKeyboardRow.add(createInlineKeyboardButton("8:00 - 15:45", "Unuseful now"));
                inlineKeyboardRow.add(createInlineKeyboardButton("16:00 - 23:45", CHOOSE_TASK_TIME + "&=" + taskId + "&=" + 16));
                break;
            case 16:
                max = 24;
                inlineKeyboardRow.add(createInlineKeyboardButton("0:00 - 7.45", CHOOSE_TASK_TIME + "&=" + taskId + "&=" + 0));
                inlineKeyboardRow.add(createInlineKeyboardButton("8:00 - 15:45", CHOOSE_TASK_TIME + "&=" + taskId + "&=" + 8));
                inlineKeyboardRow.add(createInlineKeyboardButton("16:00 - 23:45", "Unuseful now"));
                break;
        }

        inlineKeyboard.add(inlineKeyboardRow);

        while (hour < max) {
            inlineKeyboardRow = new ArrayList<>();

            while (currentTime.getTime().getHours() == hour) {
                time = new SimpleDateFormat("HH:mm").format(currentTime.getTime());

                inlineKeyboardRow.add(createInlineKeyboardButton(time,
                        SET_TASK_TIME + "&=" + taskId + "&=" + time));

                currentTime.add(java.util.Calendar.MINUTE, 15);
            }
            inlineKeyboard.add(inlineKeyboardRow);
            hour += 1;
        }
        inlineKeyboardRow = new ArrayList<>();
        inlineKeyboardRow.add(createInlineKeyboardButton("Cancel", TASK_EDIT + "&=" + taskId));

        inlineKeyboard.add(inlineKeyboardRow);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);

        return inlineKeyboardMarkup;
    }

    public List<SendMessage> setTaskTime(Task task, String message) {
        String[] timeParts = message.split(":");
        GregorianCalendar date = new GregorianCalendar();
        date.set(java.util.Calendar.HOUR, Integer.parseInt(timeParts[0]));
        date.set(java.util.Calendar.MINUTE, Integer.parseInt(timeParts[1]));

        date.set(java.util.Calendar.SECOND, 0);
        date.set(java.util.Calendar.MILLISECOND, 0);
        task.setTime(date);
        taskController.saveTask(task);

        task.getCalendar().getUser().setBot_state(BotState.NONE);
        userController.saveUser(task.getCalendar().getUser());

        SendMessage result = displayTask(task);

        result.setReplyMarkup(createChangeTaskInlineKeyboard(task.getId()));

        return List.of(result);
    }

    public List<SendMessage> deleteTask(Task task) {
        taskController.deleteTask(task);

        SendMessage message = createMessageTemplate(task.getCalendar().getUser());
        message.setText("Task deleted");

        message.setReplyMarkup(createMainKeyboard(task.getCalendar().getUser()));

        return List.of(message);
    }

    public List<SendMessage> accept(Task task) {
        task.getCalendar().getUser().setBot_state(BotState.NONE);
        userController.saveUser(task.getCalendar().getUser());

        SendMessage message = createMessageTemplate(task.getCalendar().getUser());
        message
                .setText("Main commands:");
        message.setReplyMarkup(createMainKeyboard(task.getCalendar().getUser()));

        return List.of(message);
    }

    public List<SendMessage> edit(Task task) {
        SendMessage sendMessage = displayTask(task);
        sendMessage.setReplyMarkup(createChangeTaskInlineKeyboard(task.getId()));

        return List.of(sendMessage);
    }

    public List<SendMessage> getTasksOnDate(User user, String message) {
        SendMessage messageToSend = createMessageTemplate(user);
        List<SendMessage> result = new ArrayList<SendMessage>();

        String[] command = message.split("&=");

        String d;

        final GregorianCalendar calendar = new GregorianCalendar();

        calendar.set(java.util.Calendar.HOUR, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);

        if (command.length < 2) {

            d = calendar.get(java.util.Calendar.YEAR) + "-" + (calendar.get(java.util.Calendar.MONTH) + 1) + "-" + calendar.get(java.util.Calendar.DATE);

        } else {
            d = command[2];
            String[] dP = d.split("-");
            calendar.set(Integer.parseInt(dP[2]), Integer.parseInt(dP[1]), Integer.parseInt(dP[0]));
        }
        for (Calendar c:user.getCalendars()) {
            messageToSend = createMessageTemplate(user);

            messageToSend.setText(c.getName() + ":");
            result.add(messageToSend);

            List<Task> tasks = taskController.getTasksInCalendar(c.getId()).get();
            List<Task> dateTasks = new ArrayList<>();
            tasks.stream()
                    .filter(h -> {
                        GregorianCalendar tD = h.getDate();
                        tD.set(java.util.Calendar.HOUR, 0);
                        tD.set(java.util.Calendar.MINUTE, 0);
                        tD.set(java.util.Calendar.SECOND, 0);
                        tD.set(java.util.Calendar.MILLISECOND, 0);
                        return tD.equals(calendar);
                    })
                    .forEach(dateTasks::add);

            if (dateTasks.size() == 0) {
                messageToSend = createMessageTemplate(user);

                messageToSend.setText("There's nothing");
                result.add(messageToSend);
            } else {
                for (Task t:dateTasks) {
                    messageToSend = createMessageTemplate(user);

                    messageToSend.setText(displayTask(t).getText());
                    messageToSend.setReplyMarkup(createAboutTaskInlineKeyboard(t.getId()));

                    result.add(messageToSend);
                }
            }
        }

        messageToSend = createMessageTemplate(user);

        messageToSend.setText("Main commands:");
        messageToSend.setReplyMarkup(createMainKeyboard(user));

        result.add(messageToSend);

        return result;
    }

    public List<SendMessage> changeTaskState(Task task) {
        switch (task.getTask_state()) {
            case NONE:
                task.setTask_state(TaskState.INPROGRESS);
                break;
            case INPROGRESS:
                task.setTask_state(TaskState.DONE);
                break;
        }
        taskController.saveTask(task);

        List<SendMessage> result = new ArrayList<>();

        result.add(displayTask(task));
        result.get(0).setReplyMarkup(createAboutTaskInlineKeyboard(task.getId()));

        result.add(createMessageTemplate(task.getCalendar().getUser()));
        result.get(1).setText("Main commands");
        result.get(1).setReplyMarkup(createMainKeyboard(task.getCalendar().getUser()));

        return result;
    }

    public List<SendMessage> chooseDateForQuery(User user, String message) {
        SendMessage result;
        String[] query = message.split("&=");
        InlineKeyboardMarkup inlineKeyboardMarkup;

        if (query.length < 3) {
            GregorianCalendar date = new GregorianCalendar();
            inlineKeyboardMarkup = createCalendarKeyboard(
                    date.get(java.util.Calendar.MONTH),
                    date.get(java.util.Calendar.YEAR),
                    0L,
                    CHOOSE_DATE_FOR_QUERY,
                    GET_TASKS_BY_DATE);
        } else {
            String[] dateParts = query[2].split("-");
            inlineKeyboardMarkup = createCalendarKeyboard(
                    Integer.parseInt(dateParts[0]),
                    Integer.parseInt(dateParts[1]),
                    0L,
                    CHOOSE_DATE_FOR_QUERY,
                    GET_TASKS_BY_DATE);
        }

        result = createMessageTemplate(user);
        result.setText("Choose date:");
        result.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(result);
    }
    @Override
    public BotState operatedBotState() {
        return BotState.WAIT_TASK_NAME;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(
                CHOOSE_CALENDAR,
                CREATE_TASK,
                TASK_DELETE,
                CHOOSE_DATE,
                SET_TASK_DATE,
                CHOOSE_TASK_TIME,
                SET_TASK_TIME,
                CHOOSE_PRIORITY,
                SET_PRIORITY,
                TASK_ACCEPT,
                TASK_NAME_CHANGE,
                GET_TASKS_BY_DATE,
                SET_TASK_STATE,
                CHOOSE_DATE_FOR_QUERY,
                TASK_EDIT
        );
    }
}
