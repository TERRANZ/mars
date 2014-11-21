package ru.mars.server.parser;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.mars.server.game.Player;

/**
 * Date: 01.11.14
 * Time: 22:35
 */
public class PlayerParser {
    public static void parse(Player player, Element rootElement) {
        Logger logger = Logger.getLogger(PlayerParser.class);
        if (player == null)
            return;
        NodeList playerNodeList = rootElement.getElementsByTagName("player");
        Node playerNode = playerNodeList.item(0);
        if (playerNode == null)
            logger.error("Provided xml is invalid: no player node");
        Element playerElement = (Element) playerNode;
        player.setAgility(Integer.parseInt(playerElement.getElementsByTagName("agi").item(0).getTextContent()));
        player.setArmor(Integer.parseInt(playerElement.getElementsByTagName("armor").item(0).getTextContent()));
        player.setConstitution(Integer.parseInt(playerElement.getElementsByTagName("const").item(0).getTextContent()));
        player.setLevel(Integer.parseInt(playerElement.getElementsByTagName("level").item(0).getTextContent()));
        player.setLucky(Integer.parseInt(playerElement.getElementsByTagName("lucky").item(0).getTextContent()));
        player.setMaxDamage(Integer.parseInt(playerElement.getElementsByTagName("maxdmg").item(0).getTextContent()));
        player.setMinDamage(Integer.parseInt(playerElement.getElementsByTagName("mindmg").item(0).getTextContent()));
        player.setStrength(Integer.parseInt(playerElement.getElementsByTagName("str").item(0).getTextContent()));
        player.setWeapon(Integer.parseInt(playerElement.getElementsByTagName("weapon").item(0).getTextContent()));
        player.setDefence(Integer.parseInt(playerElement.getElementsByTagName("def").item(0).getTextContent()));
        player.setMaxDefence(player.getDefence());
        player.setMaxHealth(player.getHealth());
        player.setName(playerElement.getElementsByTagName("name").item(0).getTextContent());
    }

    public static String encode(Player player) {
        StringBuilder sb = new StringBuilder();
        sb.append("<player>");
        sb.append("<name>");
        sb.append(player.getName());
        sb.append("</name>");
        sb.append("<level>");
        sb.append(player.getLevel());
        sb.append("</level>");
        sb.append("<weapon>");
        sb.append(player.getWeapon());
        sb.append("</weapon>");
        sb.append("<armor>");
        sb.append(player.getArmor());
        sb.append("</armor>");
        sb.append("<hp>");
        sb.append(player.getHealth());
        sb.append("</hp>");
        sb.append("<def>");
        sb.append(player.getDefence());
        sb.append("</def>");
        sb.append("<maxdmg>");
        sb.append(player.getMaxDamage());
        sb.append("</maxdmg>");
        sb.append("</player>");
        return sb.toString();
    }

    public static final String encodeBattlePlayer(Player player) {
        StringBuilder sb = new StringBuilder();
        sb.append("<player>");
        sb.append("<hp>");
        sb.append(player.getHealth());
        sb.append("</hp>");
        sb.append("<def>");
        sb.append(player.getDefence());
        sb.append("</def>");
        sb.append("<maxdmg>");
        sb.append(player.getMaxDamage());
        sb.append("</maxdmg>");
        sb.append("</player>");
        return sb.toString();
    }
}
