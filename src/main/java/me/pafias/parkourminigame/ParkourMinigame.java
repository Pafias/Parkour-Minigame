package me.pafias.parkourminigame;

import me.pafias.parkourminigame.commands.ParkourMinigameCommand;
import me.pafias.parkourminigame.game.Game;
import me.pafias.parkourminigame.game.GameManager;
import me.pafias.parkourminigame.game.GameState;
import me.pafias.parkourminigame.listeners.InGameListener;
import me.pafias.parkourminigame.listeners.JoinListener;
import me.pafias.parkourminigame.listeners.PreGameListener;
import me.pafias.parkourminigame.listeners.QuitListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ParkourMinigame extends JavaPlugin {

    private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static ParkourMinigame instance;

    public static ParkourMinigame getInstance() {
        return instance;
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    @Override
    public void onEnable() {
        instance = this;

        for (Player p : getServer().getOnlinePlayers())
            Users.addUser(p);

        registerCommands();
        registerListeners();

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                User user = Users.getUser(p);
                if (user.isInGame()) {
                    Game game = GameManager.getGame(user);
                    PlayerlistManager.updateTabList(user.getPlayer());
                    getServer().getScheduler().scheduleSyncDelayedTask(ParkourMinigame.getInstance(), () -> {
                        if (game.getState() == GameState.PREGAME || game.getState() == GameState.LOBBY)
                            ScoreboardManagement.setPreGameScoreboard(user.getPlayer());
                        else if (game.getState() == GameState.INGAME)
                            ScoreboardManagement.setInGameScoreboard(user.getPlayer());
                    }, 2);
                }
            }
        }, 20, 100);
    }

    @Override
    public void onDisable() {
        if (!GameManager.getGames().isEmpty())
            for (Game game : GameManager.getGames().values())
                game.stop();
        instance = null;
    }

    private void registerCommands() {
        getCommand("parkourminigame").setExecutor(new ParkourMinigameCommand());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new QuitListener(), this);
        pm.registerEvents(new PlayerlistManager(), this);
        pm.registerEvents(new PreGameListener(), this);
        pm.registerEvents(new InGameListener(), this);
    }

    public Location getLobby() {
        return new Location(getServer().getWorld(getConfig().getString("lobby.world")), getConfig().getDouble("lobby.x"), getConfig().getDouble("lobby.y"), getConfig().getDouble("lobby.z"), (float) getConfig().getDouble("lobby.yaw"), (float) getConfig().getDouble("lobby.pitch"));
    }

}
