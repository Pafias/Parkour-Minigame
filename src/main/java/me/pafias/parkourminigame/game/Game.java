package me.pafias.parkourminigame.game;

import me.pafias.parkourminigame.*;
import org.bukkit.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {

    public boolean started = false;
    private String id;
    private GameConfig config;
    private String name;
    private World world;
    private GameState gamestate;
    private int minPlayers;
    private int maxPlayers;
    private List<User> players;
    private List<Material> pickups;
    private Location spawn;
    private Location lobbyspawn;
    private int time;
    private int taskID;
    private BukkitScheduler scheduler = ParkourMinigame.getInstance().getServer().getScheduler();

    public Game(String id, World world) {
        this.id = id;
        this.config = new GameConfig(world.getName().split("_")[0]);
        this.name = config.getConfig().getString("name");
        this.world = world;
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setTime(1600);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doWeatherCycle", "false");
        this.gamestate = GameState.LOBBY;
        this.minPlayers = config.getConfig().getInt("minPlayers");
        this.maxPlayers = config.getConfig().getInt("maxPlayers");
        this.players = new ArrayList<>();
        this.pickups = new ArrayList<>(Arrays.asList(Material.REDSTONE_BLOCK, Material.OBSIDIAN, Material.LAPIS_BLOCK, Material.IRON_BLOCK, Material.GLASS, Material.DIAMOND_BLOCK));
        this.spawn = new Location(Bukkit.getWorld(config.getConfig().getString("spawn.world") + "_" + id),
                config.getConfig().getDouble("spawn.x"),
                config.getConfig().getDouble("spawn.y"),
                config.getConfig().getDouble("spawn.z"),
                (float) config.getConfig().getDouble("spawn.yaw"),
                (float) config.getConfig().getDouble("spawn.pitch"));
        this.lobbyspawn = new Location(Bukkit.getWorld(config.getConfig().getString("lobbyspawn.world") + "_" + id),
                config.getConfig().getDouble("lobbyspawn.x"),
                config.getConfig().getDouble("lobbyspawn.y"),
                config.getConfig().getDouble("lobbyspawn.z"),
                (float) config.getConfig().getDouble("lobbyspawn.yaw"),
                (float) config.getConfig().getDouble("lobbyspawn.pitch"));
    }

    public void start(boolean force) {
        if (!started) {
            if (players.size() >= minPlayers || force) {
                started = true;
                setGamestate(GameState.PREGAME);
                time = 30;
                taskID = scheduler.scheduleSyncRepeatingTask(ParkourMinigame.getInstance(), () -> {
                    if (time == 0) {
                        Bukkit.getScheduler().cancelTask(taskID);
                        setGamestate(GameState.INGAME);
                        broadcast(ChatColor.GREEN + "Game started!");
                        for (User p : getPlayers()) {
                            p.getPlayer().teleport(spawn);
                            p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
                            p.getPlayer().setExp(0);
                            p.getPlayer().setLevel(0);
                            p.getPlayer().setGameMode(GameMode.SURVIVAL);
                            p.getPlayer().setInvulnerable(false);
                        }
                        handlePickups();
                        handlePoints();
                        return;
                    }
                    for (User all : getPlayers()) {
                        all.getPlayer().setLevel(time);
                        all.getPlayer().setExp(time / (float) 30);
                    }
                    if (time == 10 || time == 5 || time == 4 || time == 3 || time == 2 || time == 1) {
                        for (User p : getPlayers()) {
                            p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
                        }
                        broadcast(ChatColor.RED + "The game will start in " + time + " seconds!");
                    }
                    time = time - 1;
                }, 0, 20);
            }
        }
    }

    private void handlePickups() {
        scheduler.scheduleSyncRepeatingTask(ParkourMinigame.getInstance(), () -> {
            if (getRandomLoc().getBlock().getType() == Material.AIR)
                getRandomLoc().getBlock().setType(pickups.get(new Random().nextInt(pickups.size())));
        }, 2, (5 * 20));
    }

    private void handlePoints() {
        scheduler.scheduleSyncRepeatingTask(ParkourMinigame.getInstance(), () -> {
            if (getRandomLoc().getBlock().getType() == Material.AIR)
                getRandomLoc().getBlock().setType(Material.DIAMOND_BLOCK);
        }, 2, (10 * 20));
    }

    private Location getRandomLoc() {
        Random r = new Random();
        double x = config.getConfig().getDouble("x-range-1") + (config.getConfig().getDouble("x-range-2") - config.getConfig().getDouble("x-range-1")) * r.nextDouble();
        double y = config.getConfig().getDouble("y-range-1") + (config.getConfig().getDouble("y-range-2") - config.getConfig().getDouble("y-range-1")) * r.nextDouble();
        double z = config.getConfig().getDouble("z-range-1") + (config.getConfig().getDouble("z-range-2") - config.getConfig().getDouble("z-range-1")) * r.nextDouble();
        return new Location(world, x, y, z);
    }

    public void stop() {
        for (User user : getPlayers()) {
            user.getPlayer().getInventory().clear();
            for (PotionEffect pe : user.getPlayer().getActivePotionEffects())
                user.getPlayer().removePotionEffect(pe.getType());
            user.getPlayer().setExp(0);
            user.getPlayer().setLevel(0);
            user.getPlayer().setGameMode(GameMode.SURVIVAL);
            user.getPlayer().setInvulnerable(false);
            user.setInGame(false);
            user.getPlayer().showPlayer(user.getPlayer());
            PlayerlistManager.resetTabList(user.getPlayer());
            ScoreboardManagement.resetScoreboard(user.getPlayer());
            user.getPlayer().teleport(ParkourMinigame.getInstance().getLobby());
        }
        world.getEntities().clear();
        Bukkit.getScheduler().cancelTasks(ParkourMinigame.getInstance());
        ParkourMinigame.getInstance().getServer().getScheduler().runTaskLater(ParkourMinigame.getInstance(), () -> RollbackHandler.delete(world.getName()), 60);
        GameManager.removeGame(this);
    }

    public void broadcast(String message) {
        for (User user : getPlayers())
            user.getPlayer().sendMessage(message);
    }

    public String getID() {
        return id;
    }

    public GameConfig getConfig() {
        return config;
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public GameState getState() {
        return gamestate;
    }

    public void setGamestate(GameState gamestate) {
        this.gamestate = gamestate;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<User> getPlayers() {
        return players;
    }

    public void setPlayers(List<User> players) {
        this.players = players;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getLobbySpawn() {
        return lobbyspawn;
    }

}
