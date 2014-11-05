package ru.mars.server.game;

/**
 * Date: 01.11.14
 * Time: 14:34
 */
public class Player {
    private String name = "";//посылать другому
    private Integer level = 0;//посылать другому
    private Integer strength = 0;
    private Integer constitution = 0; //посылать другому * 10 = здоровье
    private Integer agility = 0;
    private Integer lucky = 0;
    private Integer weapon = 0;//посылать другому
    private Integer armor = 0;//посылать другому
    private Integer minDamage = 0;
    private Integer maxDamage = 0;
    private boolean inGame = false;
    private Integer health = 0;//посылать другому
    private Integer defence = 0;//посылать другому
    private Integer attack = 0;
    private Integer maxDefence = 0;
    private Integer maxHealth = 0;

    public Player() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getStrength() {
        return strength;
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public Integer getConstitution() {
        return constitution;
    }

    public void setConstitution(Integer constitution) {
        this.constitution = constitution;
        this.health = constitution * 10;
    }

    public Integer getAgility() {
        return agility;
    }

    public void setAgility(Integer agility) {
        this.agility = agility;
    }

    public Integer getLucky() {
        return lucky;
    }

    public void setLucky(Integer lucky) {
        this.lucky = lucky;
    }

    public Integer getWeapon() {
        return weapon;
    }

    public void setWeapon(Integer weapon) {
        this.weapon = weapon;
    }

    public Integer getArmor() {
        return armor;
    }

    public void setArmor(Integer armor) {
        this.armor = armor;
    }

    public Integer getMinDamage() {
        return minDamage;
    }

    public void setMinDamage(Integer minDamage) {
        this.minDamage = minDamage;
    }

    public Integer getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(Integer maxDamage) {
        this.maxDamage = maxDamage;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public Integer getDefence() {
        return defence;
    }

    public void setDefence(Integer defence) {
        this.defence = defence;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public Integer getMaxDefence() {
        return maxDefence;
    }

    public void setMaxDefence(Integer maxDefence) {
        this.maxDefence = maxDefence;
    }

    public Integer getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(Integer maxHealth) {
        this.maxHealth = maxHealth;
    }
}
