package ru.mars.server.game;

import org.jboss.netty.channel.Channel;
import ru.mars.server.network.message.MessageFactory;

/**
 * Date: 03.11.14
 * Time: 14:15
 */
public class PairFinder implements Runnable {

//    protected Logger logger = Logger.getLogger(this.getClass());

    @Override
    public void run() {
        GameWorker gameWorker = GameWorker.getInstance();
        while (true) {
            try {
                if (GameWorker.getInstance().getPlayerMap().size() > 1) {
                    Channel chan1 = null, chan2 = null;
                    for (Channel channel : gameWorker.getPlayerMap().keySet()) {
                        if (gameWorker.getPlayerState(channel).equals(GameState.LOGGED_IN))
                            if (chan1 == null)
                                chan1 = channel;
                            else if (chan2 == null && !chan1.equals(channel) && !gameWorker.getPlayer(chan1).getToken().equals(gameWorker.getPlayer(channel).getToken()))
                                chan2 = channel;
                        if (chan1 != null && chan2 != null) {
                            try {
                                chan1.write(MessageFactory.createPairFoundMessage(1));
                                chan2.write(MessageFactory.createPairFoundMessage(2));
                                GameThread gameThread = new GameThread(chan1, chan2, gameWorker.getPlayer(chan1), gameWorker.getPlayer(chan2));
                                //добавляем для каналов игру
                                gameWorker.addGameThreadForChannel(chan1, gameThread);
                                gameWorker.addGameThreadForChannel(chan2, gameThread);
                                gameWorker.setPlayerState(chan1, GameState.GAME_LOADING);
                                gameWorker.setPlayerState(chan2, GameState.GAME_LOADING);
                            } catch (Exception e) {
//                                logger.error("Unable to send pair message", e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
//                logger.error("Error while finding pair", e);
            }
            try {
                Thread.sleep(500);//засыпаем на полсекунды, если не найдена пара
            } catch (InterruptedException e) {
//                logger.error("Interrupted while sleeping", e);
            }
        }
    }
}
