package ru.mars.server.game;

/**
 * Date: 01.11.14
 * Time: 17:40
 */
public class MessageType {
    public static final Integer C_PLAYER_INFO = 2;//статы игрокоа
    public static final Integer S_WAIT = 3;//сервер сообщает, что клиенту ищется пара
    public static final Integer C_PLAYER_CANCEL_WAIT = 4;//клиент расхотел играть
    public static final Integer S_PAIR_FOUND = 5;//пара найдена, клиент начинает прогрузку на экран игры
    public static final Integer C_READY_TO_PLAY = 6;//клиент прогрузил игру и может принимать поле и статы
    public static final Integer S_GAME_STATE = 7;//поле, статы игроков, кто первый ходит
    public static final Integer C_LINE_MOVE = 8;//движение линии
    public static final Integer S_LINE_MOVE = 9;//движениие линии другому клиенту, передаём новыи ячейки, если надо, кто дальше ходит
    public static final Integer C_OK = 10;//клиент сообщает, что получил
    public static final Integer S_LINE_DAMAGE = 11;//дамаг игроку, передаём новыи ячейки, если надо, кто дальше ходит
    public static final Integer S_GAME_OVER = 13;//игра закончена, сообщаем кто победил, кто проиграл, отсоединяем от сервера
}
