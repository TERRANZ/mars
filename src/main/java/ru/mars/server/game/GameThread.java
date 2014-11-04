package ru.mars.server.game;

import org.jboss.netty.channel.Channel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.mars.server.network.message.MessageFactory;
import ru.mars.server.network.message.MessageType;
import ru.mars.server.parser.PlayerParser;

import java.util.Date;

/**
 * Date: 01.11.14
 * Time: 21:58
 */
public class GameThread extends GameLogic implements Runnable {
    private volatile boolean game = true;

    public GameThread(Channel channel1, Channel channel2, Player player1, Player player2) {
        this.channel1 = channel1;
        this.channel2 = channel2;
        this.player1 = player1;
        this.player2 = player2;
        playerReady.put(channel1, false);
        playerReady.put(channel2, false);
        initMap();
    }

    @Override
    public void run() {
        //отсылаем игрокам карту
        //отсылаем игрокам статы противника
        sendGameStateToPlayers(true, MessageType.S_GAME_STATE);
        //ждём в цикле действий
        while (game) {

        }
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
        isAttack = false;
        checkFields();
        if (isAttack) {
            //прошла атака, надо сообщить отдельно
            sendAttackMessage();
        } else {
            sendGameStateToPlayers(false, MessageType.S_LINE_MOVE);
        }
        isSecondPlayerInMove = !isSecondPlayerInMove;
    }


    private void sendGameStateToPlayers(boolean selectMoving, int type) {
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
        StringBuilder sbFirstMove = new StringBuilder();
        if (selectMoving)
            isSecondPlayerInMove = new Date().getTime() % 2 == 0;
        sbFirstMove.append("<moveplayer>");
        sbFirstMove.append(isSecondPlayerInMove ? 2 : 1);
        sbFirstMove.append("</moveplayer>");

        StringBuilder sb1 = new StringBuilder();
        sb1.append(MessageFactory.header(type));
        sb1.append(sbMap);
        sb1.append(PlayerParser.encode(player1));
        sb1.append(sbFirstMove);
        sb1.append(MessageFactory.footer(""));
        channel2.write(sb1.toString());//второму игроку статы первого и карту
        StringBuilder sb2 = new StringBuilder();
        sb2.append(MessageFactory.header(type));
        sb2.append(sbMap);
        sb2.append(PlayerParser.encode(player2));
        sb2.append(sbFirstMove);
        sb2.append(MessageFactory.footer(""));
        channel1.write(sb2.toString());//первому игроку статы второго и карту
    }


    protected void sendAttackMessage() {
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
        StringBuilder sbDamage = new StringBuilder();

        sbDamage.append("<damage>");
        sbDamage.append(attackDamage);
        sbDamage.append("</damage>");

        StringBuilder sb1 = new StringBuilder();
        sb1.append(MessageFactory.header(MessageType.S_LINE_DAMAGE));
        sb1.append(sbMap);
        sb1.append(sbDamage);
        sb1.append(MessageFactory.footer(""));
        channel2.write(sb1.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(MessageFactory.header(MessageType.S_LINE_DAMAGE));
        sb2.append(sbMap);
        sb2.append(sbDamage);
        sb2.append(MessageFactory.footer(""));
        channel1.write(sb2.toString());
    }


}
