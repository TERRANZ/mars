package ru.mars.server.game;

import org.jboss.netty.channel.Channel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.mars.server.network.message.MessageFactory;
import ru.mars.server.network.message.MessageType;

/**
 * Date: 01.11.14
 * Time: 21:58
 */
public class GameThread extends GameLogic implements Runnable {
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
//                logger.error("Interrupted while sleep", e);
            }
        }
    }

    public synchronized void playerDisconnect(Channel channel) {
        if (channel.equals(channel1)) {
            try {
                if (channel2.isOpen())
                    channel2.write(MessageFactory.createGameOverMessage(1));
                GameWorker.getInstance().setPlayerState(channel2, GameState.LOGIN);
            } catch (Exception e) {
//                logger.error("Unable to send player disconnection message to channel2", e);
            }

        } else {
            try {
                if (channel1.isOpen())
                    channel1.write(MessageFactory.createGameOverMessage(2));
                GameWorker.getInstance().setPlayerState(channel1, GameState.LOGIN);
            } catch (Exception e) {
//                logger.error("Unable to send player disconnection message to channel1", e);
            }
        }
        game = false;
    }

    public synchronized void playerMove(Channel channel, Element rootElement) {
        NodeList moveNodeList = rootElement.getElementsByTagName("move");
        Node moveNode = moveNodeList.item(0);
        if (moveNode == null) {
//            logger.error("Provided xml is invalid: no move node");
        } else {
            Element moveElement = (Element) moveNode;
            String moveDir = moveElement.getElementsByTagName("dir").item(0).getTextContent();
            Integer moveLine = Integer.parseInt(moveElement.getElementsByTagName("line").item(0).getTextContent());
            boolean firstPlayer = channel.equals(channel1);
            String player = firstPlayer ? "player1" : "player2";
//            logger.info("Player " + player + " doing move : " + moveDir + " line = " + moveLine);

            if (firstPlayer)
                channel2.write(MessageFactory.createMoveMessage(moveDir, moveLine));
            else
                channel1.write(MessageFactory.createMoveMessage(moveDir, moveLine));

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
//                logger.error("Sleep interruption", e);
            }

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
            try {
                boolean check = checkFields(false);
                isSecondPlayerInMove = !isSecondPlayerInMove;
                channel1.write(MessageFactory.createSetMovePlayer(isSecondPlayerInMove));
                channel2.write(MessageFactory.createSetMovePlayer(isSecondPlayerInMove));
            } catch (GameOverException e) {
//                logger.debug("Game is over");
            }
        }
    }
}
