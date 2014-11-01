package ru.mars.server;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ru.mars.server.network.GameServer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main {
    private static int PORT_OF_GAME_SERVER;
    private static int PORT_OF_CHAT_SERVER;
    private static int PORT_OF_POLICY_SERVER = 1008;
    private static Logger logger = Logger.getLogger(Main.class);

    public static void main(String args[]) throws IOException, InterruptedException {
        BasicConfigurator.configure();
        initServerProperties();
        //new PolicyServer(PORT_OF_POLICY_SERVER, new String[]{"*:" + PORT_OF_GAME_SERVER + "," + PORT_OF_CHAT_SERVER}).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                new GameServer(PORT_OF_GAME_SERVER).start();
            }
        }).start();

    }

    private static void initServerProperties() throws IOException {
        logger.info("read file server.txt from " + new File("").getAbsolutePath());
        FileReader reader = new FileReader(new File("server.txt"));
        Properties properties = new Properties();
        properties.load(reader);
        PORT_OF_GAME_SERVER = Integer.valueOf(properties.getProperty("gamePort"));
        PORT_OF_CHAT_SERVER = Integer.valueOf(properties.getProperty("chatPort"));
    }
}
