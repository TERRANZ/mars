package ru.mars.server.network.message;

import ru.mars.server.game.Player;
import ru.mars.server.parser.PlayerParser;

/**
 * Date: 01.11.14
 * Time: 22:41
 */
public class MessageFactory {

    public static String header(int msgId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='utf-8'?>");
        sb.append("<msg>");
        sb.append("<id>");
        sb.append(msgId);
        sb.append("</id>");
        return sb.toString();
    }

    public static String footer(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        sb.append("</msg>");
        return sb.toString();
    }

    public static String createPingMessage() {
        return footer(header(MessageType.S_PING) + "<text> hello </text>");
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

    public static String createGameStateMessage(int[][] gemArray, int type, boolean isSecondPlayerInMove, Player player) {
        StringBuilder sbMap = new StringBuilder();
        sbMap.append("<gemArray>");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                sbMap.append(gemArray[i][j]);
                sbMap.append(",");
            }
            sbMap.delete(sbMap.length() - 1, sbMap.length());
            sbMap.append(";");
        }
        sbMap.delete(sbMap.length() - 1, sbMap.length());
        sbMap.append("</gemArray>");
        StringBuilder sbFirstMove = new StringBuilder();

        sbFirstMove.append("<moveplayer>");
        sbFirstMove.append(isSecondPlayerInMove ? 2 : 1);
        sbFirstMove.append("</moveplayer>");

        StringBuilder sb = new StringBuilder();
        sb.append(MessageFactory.header(type));
        sb.append(sbMap);
        sb.append(PlayerParser.encode(player));
        sb.append(sbFirstMove);
        sb.append(MessageFactory.footer(""));
        return sb.toString();
    }

    public static String createDamageMessage(int[][] gemArray, int attackDamage) {
        StringBuilder sbMap = new StringBuilder();
        sbMap.append("<gemArray>");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                sbMap.append(gemArray[i][j]);
                sbMap.append(",");
            }
            sbMap.delete(sbMap.length() - 1, sbMap.length());
            sbMap.append(";");
        }
        sbMap.delete(sbMap.length() - 1, sbMap.length());
        sbMap.append("</gemArray>");
        StringBuilder sbDamage = new StringBuilder();

        sbDamage.append("<damage>");
        sbDamage.append(attackDamage);
        sbDamage.append("</damage>");

        StringBuilder sb1 = new StringBuilder();
        sb1.append(MessageFactory.header(MessageType.S_LINE_DAMAGE));
        sb1.append(sbMap);
        sb1.append(sbDamage);
        sb1.append(MessageFactory.footer(""));
        return sb1.toString();
    }
}
