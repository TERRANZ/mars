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

    public PairFinder(Channel playerChannel, Player player) {
        this.playerChannel = playerChannel;
        this.player = player;
    }

    @Override
    public void run() {
        int maxDiff = 1;
        while (true) {
            for (Channel channel : GameWorker.getInstance().getPlayerMap().keySet()) {
                if (!channel.equals(playerChannel)) {
                    Player p = GameWorker.getInstance().getPlayerMap().get(channel);
                    if (!p.isInGame()) {
                        int diff = Math.abs(p.getLevel() - player.getLevel());
                        if (diff <= maxDiff) {
                            //пара найдена, разница в уровенях около 1
//                            new Thread(new GameThread(playerChannel, channel, player, p)).start();
                            channel.write(MessageFactory.createPairFoundMessage());
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
        }
    }
}
