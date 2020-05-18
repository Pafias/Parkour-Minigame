package me.pafias.parkourminigame;

import me.pafias.parkourminigame.game.Game;
import me.pafias.parkourminigame.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardManagement {

    public static void setInGameScoreboard(Player player) {
        try {
            User user = Users.getUser(player);
            Game game = GameManager.getGame(user);
            if (user != null && game != null) {
                final ScoreboardManager scoreboardmanager = Bukkit.getScoreboardManager();
                final Scoreboard scoreboard = scoreboardmanager.getNewScoreboard();
                final Objective scoreboardobjective = scoreboard.registerNewObjective("Parkour Minigame", "dummy");
                scoreboardobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                scoreboardobjective.setDisplayName(ChatColor.AQUA + "Points");
                for (User u : game.getPlayers())
                    scoreboardobjective.getScore(ChatColor.YELLOW + u.getPlayer().getName()).setScore(u.getPoints());
                scoreboardobjective.getScore(" ").setScore(-1);
                scoreboardobjective.getScore("not cubecraft.net").setScore(-2);
                player.setScoreboard(scoreboard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setPreGameScoreboard(Player player) {
        try {
            User user = Users.getUser(player);
            Game game = GameManager.getGame(user);
            if (user != null && game != null) {
                final ScoreboardManager scoreboardmanager = Bukkit.getScoreboardManager();
                final Scoreboard scoreboard = scoreboardmanager.getNewScoreboard();
                final Objective scoreboardobjective = scoreboard.registerNewObjective("Parkour Minigame", "dummy");
                scoreboardobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                scoreboardobjective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Parkour Minigame");
                scoreboardobjective.getScore("  ").setScore(8);
                scoreboardobjective.getScore(ChatColor.DARK_PURPLE + "Map:").setScore(7);
                scoreboardobjective.getScore(ChatColor.GREEN + game.getWorld().getName().split("_")[0]).setScore(6);
                scoreboardobjective.getScore(" ").setScore(5);
                scoreboardobjective.getScore(ChatColor.DARK_PURPLE + "Players:").setScore(4);
                scoreboardobjective.getScore(ChatColor.GREEN + "" + game.getPlayers().size() + "/" + game.getMaxPlayers()).setScore(3);
                scoreboardobjective.getScore("").setScore(2);
                scoreboardobjective.getScore("not cubecraft.net").setScore(1);
                player.setScoreboard(scoreboard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetScoreboard(Player player) {
        try {
            final ScoreboardManager scoreboardmanager = Bukkit.getScoreboardManager();
            final Scoreboard scoreboard = scoreboardmanager.getNewScoreboard();
            final Objective scoreboardobjective = scoreboard.registerNewObjective("Parkour Minigame", "dummy");
            scoreboardobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
            scoreboardobjective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Parkour Minigame");
            scoreboardobjective.unregister();
            player.setScoreboard(scoreboard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
