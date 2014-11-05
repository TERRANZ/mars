package ru.mars.server.game;

import org.jboss.netty.channel.Channel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.mars.server.network.message.MessageFactory;
import ru.mars.server.network.message.MessageType;

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
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.error("Interrupted while sleep", e);
            }
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
            if (player1.getHealth() <= 0 || player2.getHealth() <= 0) {
                game = false;
                sendGameOverMessage();
            }
        } else {
            sendGameStateToPlayers(false, MessageType.S_LINE_MOVE);
        }
        isSecondPlayerInMove = !isSecondPlayerInMove;
    }


    private void sendGameStateToPlayers(boolean selectMoving, int type) {
        if (selectMoving)
            isSecondPlayerInMove = new Date().getTime() % 2 == 0;
        channel2.write(MessageFactory.createGameStateMessage(gemArray, type, isSecondPlayerInMove, player1));//второму игроку статы первого и карту
        channel1.write(MessageFactory.createGameStateMessage(gemArray, type, isSecondPlayerInMove, player2));//первому игроку статы второго и карту
    }


    private void sendAttackMessage() {
        channel2.write(MessageFactory.createDamageMessage(gemArray, attackDamage));
        channel1.write(MessageFactory.createDamageMessage(gemArray, attackDamage));
    }

    private void sendGameOverMessage() {
        channel1.write(MessageFactory.createGameOverMessage());
        channel2.write(MessageFactory.createGameOverMessage());
    }
}
