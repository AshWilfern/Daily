package com.telegram.bot.entity;

import com.telegram.bot.util.BotState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends com.telegram.bot.entity.DailyEntity {
    @Column(name = "chat_id", unique = true, nullable = false)
    private long chatId;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "bot_state", nullable = false)
    private BotState bot_state;

    @Column(name = "work_with", nullable = true)
    private Long work_with;

    @Column(name = "work_with_message", nullable = true)
    private Integer messageId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<Calendar> calendars;

    public User(long chatId) {
        this.chatId = chatId;
        this.name = String.valueOf(chatId);
        this.bot_state = BotState.START;
    }
}
