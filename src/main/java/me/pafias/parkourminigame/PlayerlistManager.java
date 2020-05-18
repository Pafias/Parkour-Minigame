package me.pafias.parkourminigame;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerlistManager implements Listener {

    public static void updateTabList(Player player) {
        try {
            User user = Users.getUser(player);
            player.setPlayerListName(user.getPlayer().getName() + " [" + user.getPoints() + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetTabList(Player player) {
        try {
            player.setPlayerListName(player.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updateTabList(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        resetTabList(event.getPlayer());
    }

}
