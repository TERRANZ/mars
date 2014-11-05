package ru.mars.server.game;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import ru.mars.server.network.message.MessageFactory;
import ru.mars.server.network.message.MessageType;
import ru.mars.server.parser.PlayerParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Date: 01.11.14
 * Time: 14:32
 */
public class GameWorker {
    private static GameWorker instance = new GameWorker();
    private Logger logger = Logger.getLogger(this.getClass());
    private Map<Channel, GameState> gameStateMap = new HashMap<>();
    private Map<Channel, Player> playerMap = new HashMap<>();
    private Map<Future, Channel> finders = new WeakHashMap<>();
    private ExecutorService service;
    private Map<Channel, GameThread> gameThreadMap = new HashMap<>();

    private GameWorker() {
        service = Executors.newFixedThreadPool(10);
    }

    public static GameWorker getInstance() {
        return instance;
    }

    public synchronized void addPlayer(Channel channel) {
        gameStateMap.put(channel, GameState.LOGIN);
        playerMap.put(channel, new Player());
    }

    public synchronized void removePlayer(Channel channel) {
        gameStateMap.remove(channel);
        playerMap.remove(channel);
    }

    public synchronized void handlePlayerCommand(Channel channel, String xml) {
        logger.info("Received xml = " + xml + " from channel " + channel.toString());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            logger.error("Unable to parse xml", e);
        }

        if (doc != null) {
            Element root = doc.getDocumentElement();
            Integer command = Integer.parseInt(root.getElementsByTagName("id").item(0).getTextContent());
            switch (command) {
                case MessageType.C_PING: {
                    logger.info("PING");
                    channel.write(MessageFactory.createPingMessage());
                }
                break;

                case MessageType.C_PLAYER_INFO: {
                    if (!gameStateMap.get(channel).equals(GameState.LOGIN))
                        return;//TODO: exception?
                    PlayerParser.parse(playerMap.get(channel), root);
                    gameStateMap.put(channel, GameState.LOGGED_IN);
                    channel.write(MessageFactory.createWaitMessage());
                    PairFinder pairFinder = new PairFinder(channel, playerMap.get(channel));
                    finders.put(service.submit(pairFinder), channel);
                }
                break;
                case MessageType.C_PLAYER_CANCEL_WAIT: {
                    if (!gameStateMap.get(channel).equals(GameState.LOGGED_IN))
                        return;//TODO: exception?
                    gameStateMap.remove(channel);
                    channel.write(MessageFactory.createGameOverMessage());
                    for (Future pairFinder : finders.keySet())
                        if (finders.get(pairFinder).equals(channel))
                            pairFinder.cancel(true);//стопаем поиск пары если игрок отказался
                }
                break;
                case MessageType.C_READY_TO_PLAY: {
                    if (!gameStateMap.get(channel).equals(GameState.GAME_LOADING))
                        return;//TODO: exception?
                    gameStateMap.put(channel, GameState.IN_GAME);
                    GameThread gameThread = gameThreadMap.get(channel);
                    if (gameThread != null) {
                        gameThread.setPlayerReady(channel);
                        if (gameThread.isAllReady())
                            new Thread(gameThread).start();//запускам игру, оба игрока готовы
                    }

                }
                break;
                case MessageType.C_LINE_MOVE: {
                    gameThreadMap.get(channel).playerMove(channel, root);
                }
                break;
                case MessageType.C_OK: {
                    gameThreadMap.get(channel).playerOk(channel, root);
                }
                break;
            }
        }
    }

    public Map<Channel, Player> getPlayerMap() {
        synchronized (playerMap) {
            return playerMap;
        }
    }

    public void addGameThreadForChannel(Channel channel, GameThread gameThread) {
        synchronized (gameThreadMap) {
            gameThreadMap.put(channel, gameThread);
        }
    }

    public void setPlayerState(Channel channel, GameState gameState) {
        synchronized (gameStateMap) {
            gameStateMap.put(channel, gameState);
        }
    }
}
