package ru.mars.server.test;

import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ru.mars.server.game.Player;
import ru.mars.server.network.message.MessageFactory;
import ru.mars.server.network.message.MessageType;

import java.util.Random;

/**
 * Date: 04.11.14
 * Time: 18:46
 */
public class XMLOutputTest extends TestCase {
    public void test1() {
        BasicConfigurator.configure();
        Logger logger = Logger.getLogger(this.getClass());
        logger.info("Pair found message");
        logger.info(MessageFactory.createPairFoundMessage(1));
        logger.info("GameOver message");
        logger.info(MessageFactory.createGameOverMessage(1));


        int[][] gemArray = new int[8][8];

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                gemArray[i][j] = randInt(1, 6);

        logger.info("Damage message");
        logger.info(MessageFactory.createDamageMessage(gemArray, 123, new Player(), new Player(), true));

        logger.info("Game state message");
        logger.info(MessageFactory.createGameStateMessage(gemArray, MessageType.S_GAME_STATE, new Player(), new Player(), true));
        logger.info("Game state message");
        logger.info(MessageFactory.createGameStateMessage(gemArray, MessageType.S_GAME_STATE, new Player(), new Player(), false));
    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
