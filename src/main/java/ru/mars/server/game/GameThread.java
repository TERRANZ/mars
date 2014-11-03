package ru.mars.server.game;

import org.jboss.netty.channel.Channel;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 01.11.14
 * Time: 21:58
 */
public class GameThread implements Runnable {

    private Channel channel1, channel2;
    private Player player1, player2;
    private int[][] map = new int[8][8];
    private Map<Channel, Boolean> playerReady = new HashMap<>();
    private volatile boolean game = true;

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
        //ждём в цикле действий
        while (game) {

        }
    }

    private void initMap() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                map[i][j] = 0;
    }

    public int[][] getMap() {
        return map;
    }

    public synchronized void setPlayerReady(Channel playerChannel) {
        playerReady.put(playerChannel, true);
    }

    public synchronized boolean isAllReady() {
        return playerReady.get(channel1) && playerReady.get(channel2);
    }

    public synchronized void playerMove(Channel channel, Element data) {
    }

    public synchronized void playerOk(Channel channel, Element root) {

    }
}
