package ru.mars.server.network.message;

/**
 * Date: 01.11.14
 * Time: 22:41
 */
public class MessageFactory {

    public static String header(int msgId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        sb.append("<msg>");
        sb.append("<id>");
        sb.append(msgId);
        sb.append("</id");
        return sb.toString();
    }

    public static String footer(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        sb.append("</msg>");
        return sb.toString();
    }

    public static String createWaitMessage() {
        return footer(header(MessageType.S_WAIT));
    }

    public static String createGameOverMessage() {
        return footer(header(MessageType.S_GAME_OVER));
    }

    public static String createPairFoundMessage() {
        return footer(header(MessageType.S_PAIR_FOUND));
    }
}
