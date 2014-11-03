package ru.mars.server.game;

import org.jboss.netty.channel.Channel;

/**
 * Date: 01.11.14
 * Time: 21:58
 */
public class GameThread implements Runnable {

    private Channel channel1, channel2;
    private Player player1, player2;
    private int[][] map = new int[8][8];

    public GameThread(Channel channel1, Channel channel2, Player player1, Player player2) {
        this.channel1 = channel1;
        this.channel2 = channel2;
        this.player1 = player1;
        this.player2 = player2;
    }

    @Override
    public void run() {

    }

    private void initMap() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                map[i][j] = 0;
    }

    public int[][] getMap() {
        return map;
    }
}
