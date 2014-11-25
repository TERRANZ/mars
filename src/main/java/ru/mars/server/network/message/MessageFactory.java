package ru.mars.server.network.message;

import ru.mars.server.game.Player;
import ru.mars.server.game.Statistic;
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

    public static String createPingMessage(Statistic statistic) {
        return footer(header(MessageType.S_PING) + "<text> hello </text> <online>" + statistic.getOnline() + "</online><games>" + statistic.getGames() + "</games>");
    }

    public static String createWaitMessage() {
        return footer(header(MessageType.S_WAIT));
    }

    public static String createGameOverMessage(Integer deadPlayer) {
        return footer(header(MessageType.S_GAME_OVER) + "<deadplayer>" + deadPlayer + "</deadplayer>");
    }

    public static String createPairFoundMessage(int playerNum) {
        return footer(header(MessageType.S_PAIR_FOUND) + "<playerid>" + playerNum + "</playerid>");
    }

    public static String createGameStateMessage(int[][] gemArray, int type, boolean isSecondPlayerInMove, Player enemy, Player my, boolean selectMoving) {
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

        if (selectMoving)
            sb.append(PlayerParser.encode("enemy", enemy));
        else
            sb.append(PlayerParser.encodeBattlePlayer("enemy", enemy));

        if (selectMoving)
            sb.append(PlayerParser.encode("player", my));
        else
            sb.append(PlayerParser.encodeBattlePlayer("player", my));

        sb.append(sbFirstMove);
        sb.append(MessageFactory.footer(""));
        return sb.toString();
    }

    public static String createDamageMessage(int[][] gemArray, int attackDamage, boolean isSecondPlayerInMove, Player enemy, Player my) {
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

        StringBuilder sbDamage = new StringBuilder();

        sbDamage.append("<damage>");
        sbDamage.append(attackDamage);
        sbDamage.append("</damage>");

        StringBuilder sb1 = new StringBuilder();
        sb1.append(MessageFactory.header(MessageType.S_LINE_DAMAGE));
        sb1.append(sbMap);
        sb1.append(sbDamage);
        sb1.append(sbFirstMove);
        sb1.append(PlayerParser.encodeBattlePlayer("player", my));
        sb1.append(PlayerParser.encodeBattlePlayer("enemy", enemy));
        sb1.append(MessageFactory.footer(""));
        return sb1.toString();
    }

    public static String createMoveMessage(String dir, Integer line) {
        return footer(header(MessageType.S_PLAYER_MOVE) + "<move><dir>" + dir + "</dir><line>" + line + "</line></move>");
    }
}
