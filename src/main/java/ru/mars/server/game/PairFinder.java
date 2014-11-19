package ru.mars.server.game;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import ru.mars.server.network.message.MessageFactory;

/**
 * Date: 03.11.14
 * Time: 14:15
 */
public class PairFinder implements Runnable {

    private Channel playerChannel;
    private Player player;
    protected Logger logger = Logger.getLogger(this.getClass());

    public PairFinder(Channel playerChannel, Player player) {
        this.playerChannel = playerChannel;
        this.player = player;
    }

    @Override
    public void run() {
        int maxDiff = 1;
        while (true) {
            for (Channel channel : GameWorker.getInstance().getPlayerMap().keySet()) {
                if (!channel.equals(playerChannel) && GameWorker.getInstance().getPlayerState(channel).equals(GameState.LOGGED_IN)) {
                    Player p = GameWorker.getInstance().getPlayerMap().get(channel);
                    if (!p.isInGame()) {
                        int diff = Math.abs(p.getLevel() - player.getLevel());
                        if (diff <= maxDiff) {
                            //пара найдена, разница в уровенях около 1
//                            new Thread(new GameThread(playerChannel, channel, player, p)).start();
                            try {
                                playerChannel.write(MessageFactory.createPairFoundMessage(1));
                                channel.write(MessageFactory.createPairFoundMessage(2));
                                GameThread gameThread = new GameThread(playerChannel, channel, player, p);
                                //добавляем для каналов игру
                                GameWorker.getInstance().addGameThreadForChannel(playerChannel, gameThread);
                                GameWorker.getInstance().addGameThreadForChannel(channel, gameThread);
                                GameWorker.getInstance().setPlayerState(playerChannel, GameState.GAME_LOADING);
                                GameWorker.getInstance().setPlayerState(channel, GameState.GAME_LOADING);
                            } catch (Exception e) {
                                logger.error("Unable to send pair message", e);
                            }

                            return;
                        }
                    }
                }
            }
            try {
                Thread.sleep(60000);//засыпаем на минуту, если не найдена пара и увеличиваем разброс
            } catch (InterruptedException e) {
                Logger.getLogger(this.getClass()).error("Interrupted while sleeping", e);
            }
            maxDiff++;
            if (maxDiff > 2)
                maxDiff = 1;
        }
    }
}
