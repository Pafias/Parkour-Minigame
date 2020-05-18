package me.pafias.parkourminigame;

import org.bukkit.entity.Player;

public class User {

    private Player player;
    private UserConfig config;
    private boolean isInGame;
    private int totalPoints;
    private int points;

    public User(Player player) {
        this.player = player;
        this.config = new UserConfig(player.getUniqueId());
        this.totalPoints = config.getConfig().getInt("totalPoints");
        this.points = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public UserConfig getConfig() {
        return config;
    }

    public boolean isInGame() {
        return isInGame;
    }

    public void setInGame(boolean inGame) {
        this.isInGame = inGame;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
        config.getConfig().set("totalPoints", totalPoints);
        config.saveConfig();
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
        player.setLevel(points);
        player.setExp(points / (float) 30);
    }
}
