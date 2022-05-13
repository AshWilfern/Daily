DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS calendars;
DROP TABLE IF EXISTS users;
CREATE SEQUENCE global_seq START WITH 100000;

CREATE TABLE users
(
    id         INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    chat_id    INTEGER UNIQUE                NOT NULL,
    user_name  VARCHAR                       NOT NULL,
    bot_state  VARCHAR                       NOT NULL,
    work_with  INTEGER,
    work_with_message INTEGER
);

CREATE TABLE calendars
(
    id             INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    user_id        INTEGER NOT NULL,
    calendar_name  VARCHAR NOT NULL
);

CREATE TABLE tasks
(
    id             INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    task_name      VARCHAR NOT NULL,
    calendar_id    INTEGER NOT NULL,
    description    VARCHAR,
    "date"         DATE NOT NULL,
    "time"         TIME NOT NULL,
    priority       INTEGER,
    task_state     INTEGER NOT NULL
);