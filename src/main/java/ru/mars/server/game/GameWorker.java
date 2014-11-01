package ru.mars.server.game;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Date: 01.11.14
 * Time: 14:32
 */
public class GameWorker {
    private static GameWorker instance = new GameWorker();
    private List<Player> players = new LinkedList<>();
    private Logger logger = Logger.getLogger(this.getClass());

    public static GameWorker getInstance() {
        return instance;
    }

    public synchronized void addPlayer(String... params) {
    }

    public synchronized void removePlayer(String... params) {
    }

    public synchronized void handlePlayerCommand(String xml) {
        logger.info("Received xml = " + xml);
    }
}
