package ru.mars.server.game;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.mars.server.network.message.MessageFactory;
import ru.mars.server.network.message.MessageType;
import ru.mars.server.parser.PlayerParser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Date: 01.11.14
 * Time: 21:58
 */
public class GameThread implements Runnable {

    private Channel channel1, channel2;
    private Player player1, player2;
    private int[][] gemArray = new int[8][8];
    private Map<Channel, Boolean> playerReady = new HashMap<>();
    private volatile boolean game = true;
    private Logger logger = Logger.getLogger(this.getClass());
    private boolean isSecondPlayerInMove = false;

    public GameThread(Channel channel1, Channel channel2, Player player1, Player player2) {
        this.channel1 = channel1;
        this.channel2 = channel2;
        this.player1 = player1;
        this.player2 = player2;
        playerReady.put(channel1, false);
        playerReady.put(channel2, false);
    }

    @Override
    public void run() {
        //отсылаем игрокам карту
        //отсылаем игрокам статы противника
        sendGameStateToPlayers();
        //ждём в цикле действий
        while (game) {

        }
    }

    private void sendGameStateToPlayers() {
        StringBuilder sbMap = new StringBuilder();
        sbMap.append("<gemArray>");
        for (int i = 0; i < 8; i++) {
            sbMap.append("<line");
            sbMap.append(i);
            sbMap.append(">");
            for (int j = 0; j < 8; j++) {
                sbMap.append(gemArray[i][j]);
                sbMap.append(",");
            }
            sbMap.delete(sbMap.length() - 1, sbMap.length());
            sbMap.append("</line");
            sbMap.append(i);
            sbMap.append(">");
        }
        sbMap.append("</gemArray>");
        StringBuilder sbFistMove = new StringBuilder();
        sbFistMove.append("<first>");
        isSecondPlayerInMove = new Date().getTime() % 2 == 0;
        sbFistMove.append(isSecondPlayerInMove ? 2 : 1);
        sbFistMove.append("</first>");
        StringBuilder sb1 = new StringBuilder();
        sb1.append(MessageFactory.header(MessageType.S_GAME_STATE));
        sb1.append(sbMap);
        sb1.append(PlayerParser.encode(player1));
        sb1.append(sbFistMove);
        sb1.append(MessageFactory.footer(""));
        channel2.write(sb1.toString());//второму игроку статы первого и карту
        StringBuilder sb2 = new StringBuilder();
        sb2.append(MessageFactory.header(MessageType.S_GAME_STATE));
        sb2.append(sbMap);
        sb2.append(PlayerParser.encode(player2));
        sb2.append(sbFistMove);
        sb2.append(MessageFactory.footer(""));
        channel1.write(sb2.toString());//первому игроку статы второго и карту
    }

    private void initMap() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                gemArray[i][j] = randInt(1, 6);
    }

    public int[][] getMap() {
        return gemArray;
    }

    public synchronized void setPlayerReady(Channel playerChannel) {
        playerReady.put(playerChannel, true);
    }

    public synchronized boolean isAllReady() {
        return playerReady.get(channel1) && playerReady.get(channel2);
    }

    public synchronized void playerMove(Channel channel, Element rootElement) {
        NodeList moveNodeList = rootElement.getElementsByTagName("move");
        Node moveNode = moveNodeList.item(0);
        if (moveNode == null)
            logger.error("Provided xml is invalid: no move node");
        Element moveElement = (Element) moveNode;
        String moveDir = moveElement.getElementsByTagName("dir").item(0).getTextContent();
        Integer moveLine = Integer.parseInt(moveElement.getElementsByTagName("line").item(0).getTextContent());
        boolean firstPlayer = channel.equals(channel1);
        String player = firstPlayer ? "player1" : "player2";
        logger.info("Player " + player + " doing move : " + moveDir + " line = " + moveLine);
        switch (moveDir) {
            case "l": {
                moveLineLeft(moveLine);
            }
            break;
            case "r": {
                moveLineRight(moveLine);
            }
            break;
            case "u": {
                moveLineUp(moveLine);
            }
            break;
            case "d": {
                moveLineDown(moveLine);
            }
            break;
        }
        checkFields();
    }

    public synchronized void playerOk(Channel channel, Element root) {

    }

    private void moveLineLeft(int count) {
        int el0 = gemArray[0][count];
        for (int i = 0; i < 8; i++) {
            if (i != 0) {
                gemArray[i - 1][count] = gemArray[i][count];
            }
        }
        gemArray[7][count] = el0;
    }

    private void moveLineRight(int count) {
        int el0 = gemArray[7][count];
        for (int i = 7; i >= 0; i--) {
            if (i != 7) {
                gemArray[i + 1][count] = gemArray[i][count];
            }
        }
        gemArray[0][count] = el0;
    }

    private void moveLineUp(int count) {
        int el0 = gemArray[count][0];
        for (int i = 0; i < 8; i++) {
            if (i != 0) {
                gemArray[count][i - 1] = gemArray[count][i];
            }
        }
        gemArray[count][7] = el0;
    }

    private void moveLineDown(int count) {
        int el0 = gemArray[count][7];
        for (int i = 7; i >= 0; i--) {
            if (i != 7) {
                gemArray[count][i + 1] = gemArray[count][i];
            }
        }
        gemArray[count][0] = el0;
    }

    private Boolean tryCheckVLine5(boolean update) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int count = (gemArray[i][j]);
                if (j + 4 <= 7) {
                    if (count == gemArray[i][j + 1] && count == gemArray[i][j + 2] && count == gemArray[i][j + 3] && count == gemArray[i][j + 4]) {
                        if (update) {
                            gemArray[i][j + 1] = randInt(1, 6);
                            gemArray[i][j + 2] = randInt(1, 6);
                            gemArray[i][j + 3] = randInt(1, 6);
                            gemArray[i][j + 4] = randInt(1, 6);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Boolean tryCheckVLine4(boolean remove) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int count = (gemArray[i][j]);
                if (j + 3 <= 7) {
                    if (count == gemArray[i][j + 1] && count == gemArray[i][j + 2] && count == gemArray[i][j + 3]) {
                        if (remove) {
                            gemArray[i][j + 1] = randInt(1, 6);
                            gemArray[i][j + 2] = randInt(1, 6);
                            gemArray[i][j + 3] = randInt(1, 6);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Boolean tryCheckVLine3(boolean remove) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int count = (gemArray[i][j]);
                if (j + 2 <= 7) {
                    if (count == gemArray[i][j + 1] && count == gemArray[i][j + 2]) {
                        if (remove) {
                            gemArray[i][j + 1] = randInt(1, 6);
                            gemArray[i][j + 2] = randInt(1, 6);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Boolean tryCheckHLine5(boolean remove) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //trace(i, j)
                int count = (gemArray[i][j]);
                if (i + 4 <= 7) {
                    if (count == gemArray[i + 1][j] && count == gemArray[i + 2][j] && count == gemArray[i + 3][j] && count == gemArray[i + 4][j]) {
                        if (remove) {
                            gemArray[i + 1][j] = randInt(1, 6);
                            gemArray[i + 2][j] = randInt(1, 6);
                            gemArray[i + 3][j] = randInt(1, 6);
                            gemArray[i + 4][j] = randInt(1, 6);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Boolean tryCheckHLine4(boolean remove) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int count = (gemArray[i][j]);
                if (i + 3 <= 7) {
                    if (count == gemArray[i + 1][j] && count == gemArray[i + 2][j] && count == gemArray[i + 3][j]) {
                        if (remove) {
                            gemArray[i + 1][j] = randInt(1, 6);
                            gemArray[i + 2][j] = randInt(1, 6);
                            gemArray[i + 3][j] = randInt(1, 6);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Boolean tryCheckHLine3(boolean remove) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int count = (gemArray[i][j]);
                if (i + 2 <= 7) {
                    if (count == gemArray[i + 1][j] && count == gemArray[i + 2][j]) {
                        if (remove) {
                            gemArray[i + 1][j] = randInt(1, 6);
                            gemArray[i + 2][j] = randInt(1, 6);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void checkFields() {
        int linesFound = 0;
        if (tryCheckHLine5(false)) {
            tryCheckHLine5(true);
            linesFound++;
        } else {
            if (tryCheckHLine4(false)) {
                tryCheckHLine4(true);
                linesFound++;
            } else {
                if (tryCheckHLine3(false)) {
                    tryCheckHLine3(true);
                    linesFound++;
                } else {
                    if (tryCheckVLine5(false)) {
                        tryCheckVLine5(true);
                        linesFound++;
                    } else {
                        if (tryCheckVLine4(false)) {
                            tryCheckVLine4(true);
                            linesFound++;
                        } else {
                            if (tryCheckVLine3(false)) {
                                tryCheckVLine3(true);
                                linesFound++;
                            }
                        }
                    }
                }
            }
        }
        if (linesFound == 0) {
            isSecondPlayerInMove = !isSecondPlayerInMove;
        } else {
            linesFound = 0;
        }

    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
