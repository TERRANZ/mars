package ru.mars.server.game;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Date: 04.11.14
 * Time: 18:34
 */
public abstract class GameLogic {
    protected Channel channel1, channel2;
    protected Player player1, player2;
    protected int[][] gemArray = new int[8][8];
    protected Map<Channel, Boolean> playerReady = new HashMap<>();
    protected Logger logger = Logger.getLogger(this.getClass());
    protected boolean isSecondPlayerInMove = false;
    protected boolean isAttack = false;
    protected int attackDamage = 0;

    protected void initMap() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                gemArray[i][j] = randInt(1, 6);
    }

    public int[][] getMap() {
        return gemArray;
    }

    public synchronized void setPlayerReady(Channel playerChannel) {
        playerReady.put(playerChannel, true);
    }

    public synchronized boolean isAllReady() {
        return playerReady.get(channel1) && playerReady.get(channel2);
    }

    public synchronized void playerOk(Channel channel, Element root) {

    }

    protected void moveLineLeft(int count) {
        int el0 = gemArray[0][count];
        for (int i = 0; i < 8; i++) {
            if (i != 0) {
                gemArray[i - 1][count] = gemArray[i][count];
            }
        }
        gemArray[7][count] = el0;
    }

    protected void moveLineRight(int count) {
        int el0 = gemArray[7][count];
        for (int i = 7; i >= 0; i--) {
            if (i != 7) {
                gemArray[i + 1][count] = gemArray[i][count];
            }
        }
        gemArray[0][count] = el0;
    }

    protected void moveLineUp(int count) {
        int el0 = gemArray[count][0];
        for (int i = 0; i < 8; i++) {
            if (i != 0) {
                gemArray[count][i - 1] = gemArray[count][i];
            }
        }
        gemArray[count][7] = el0;
    }

    protected void moveLineDown(int count) {
        int el0 = gemArray[count][7];
        for (int i = 7; i >= 0; i--) {
            if (i != 7) {
                gemArray[count][i + 1] = gemArray[count][i];
            }
        }
        gemArray[count][0] = el0;
    }

    protected Boolean tryCheckVLine5(boolean update) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int count = (gemArray[i][j]);
                if (j + 4 <= 7) {
                    if (count == gemArray[i][j + 1] && count == gemArray[i][j + 2] && count == gemArray[i][j + 3] && count == gemArray[i][j + 4]) {
                        if (update) {
                            gemArray[i][j] = randInt(1, 6);
                            gemArray[i][j + 1] = randInt(1, 6);
                            gemArray[i][j + 2] = randInt(1, 6);
                            gemArray[i][j + 3] = randInt(1, 6);
                            gemArray[i][j + 4] = randInt(1, 6);
                            doAction(count, 2);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected Boolean tryCheckVLine4(boolean remove) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int count = (gemArray[i][j]);
                if (j + 3 <= 7) {
                    if (count == gemArray[i][j + 1] && count == gemArray[i][j + 2] && count == gemArray[i][j + 3]) {
                        if (remove) {
                            gemArray[i][j] = randInt(1, 6);
                            gemArray[i][j + 1] = randInt(1, 6);
                            gemArray[i][j + 2] = randInt(1, 6);
                            gemArray[i][j + 3] = randInt(1, 6);
                            doAction(count, 1);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected Boolean tryCheckVLine3(boolean remove) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int count = (gemArray[i][j]);
                if (j + 2 <= 7) {
                    if (count == gemArray[i][j + 1] && count == gemArray[i][j + 2]) {
                        if (remove) {
                            gemArray[i][j] = randInt(1, 6);
                            gemArray[i][j + 1] = randInt(1, 6);
                            gemArray[i][j + 2] = randInt(1, 6);
                            doAction(count, 0);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected Boolean tryCheckHLine5(boolean remove) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //trace(i, j)
                int count = (gemArray[i][j]);
                if (i + 4 <= 7) {
                    if (count == gemArray[i + 1][j] && count == gemArray[i + 2][j] && count == gemArray[i + 3][j] && count == gemArray[i + 4][j]) {
                        if (remove) {
                            gemArray[i][j] = randInt(1, 6);
                            gemArray[i + 1][j] = randInt(1, 6);
                            gemArray[i + 2][j] = randInt(1, 6);
                            gemArray[i + 3][j] = randInt(1, 6);
                            gemArray[i + 4][j] = randInt(1, 6);
                            doAction(count, 2);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected Boolean tryCheckHLine4(boolean remove) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int count = (gemArray[i][j]);
                if (i + 3 <= 7) {
                    if (count == gemArray[i + 1][j] && count == gemArray[i + 2][j] && count == gemArray[i + 3][j]) {
                        if (remove) {
                            gemArray[i][j] = randInt(1, 6);
                            gemArray[i + 1][j] = randInt(1, 6);
                            gemArray[i + 2][j] = randInt(1, 6);
                            gemArray[i + 3][j] = randInt(1, 6);
                            doAction(count, 1);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected Boolean tryCheckHLine3(boolean remove) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int count = (gemArray[i][j]);
                if (i + 2 <= 7) {
                    if (count == gemArray[i + 1][j] && count == gemArray[i + 2][j]) {
                        if (remove) {
                            gemArray[i][j] = randInt(1, 6);
                            gemArray[i + 1][j] = randInt(1, 6);
                            gemArray[i + 2][j] = randInt(1, 6);
                            doAction(count, 0);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void checkFields() {
        int linesFound = 0;
        if (tryCheckHLine5(false)) {
            tryCheckHLine5(true);
            linesFound++;
        } else {
            if (tryCheckHLine4(false)) {
                tryCheckHLine4(true);
                linesFound++;
            } else {
                if (tryCheckHLine3(false)) {
                    tryCheckHLine3(true);
                    linesFound++;
                } else {
                    if (tryCheckVLine5(false)) {
                        tryCheckVLine5(true);
                        linesFound++;
                    } else {
                        if (tryCheckVLine4(false)) {
                            tryCheckVLine4(true);
                            linesFound++;
                        } else {
                            if (tryCheckVLine3(false)) {
                                tryCheckVLine3(true);
                                linesFound++;
                            }
                        }
                    }
                }
            }
        }
        if (linesFound != 0) {
            linesFound = 0;
        }
    }

    protected void doAction(int type, int bonus) {
        if (type == 1) {
            isAttack = true;
            attackDamage = getAttack(bonus);
        }
    }

    protected int getAttack(int bonus) {
        int dmg = 0;
        if (!isSecondPlayerInMove) {
            int hp = player2.getHealth();
            int def = player2.getDefence();
            dmg = generateHeroDamage(player1.getStrength(), player1.getAgility(), player1.getMinDamage(), player1.getMaxDamage()/*+ PlayerCore.heroAtk*/, bonus);
            def -= dmg;
            if (def < 0) {
                player2.setDefence(0);
                hp -= (-def);
                player2.setHealth(hp);
            } else {
                player2.setDefence(def);
            }
        } else {
            int hp = player1.getHealth();
            int def = player1.getDefence();
            dmg = generateHeroDamage(player2.getStrength(), player2.getAgility(), player2.getMinDamage(), player2.getMaxDamage()/*+ PlayerCore.heroAtk*/, bonus);
            def -= dmg;
            if (def < 0) {
                player1.setDefence(0);
                hp -= (-def);
                player1.setHealth(hp);
            } else {
                player1.setDefence(def);
            }
        }
        return dmg;
    }


    protected int generateHeroDamage(int strength, int agility, int min, int max, int bonus) {
        int genDmg = (int) (min + Math.round(Math.random() * (max - min)));
        if (genDmg < max) {
            int str = ((strength + bonus * 3) / 100);
            int luck = (int) Math.random();
            if (luck < str) {
                genDmg = max;
            }
        }
        int luck2 = (int) Math.random();
        int agl = (int) ((agility + bonus * 2.5) / 100);
        if (luck2 <= agl) {
            genDmg = (genDmg + bonus) * 2;
        } else {
            genDmg = genDmg + bonus;
        }
        return genDmg;
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
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