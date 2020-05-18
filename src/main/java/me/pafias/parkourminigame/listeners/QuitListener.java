package me.pafias.parkourminigame.listeners;

import me.pafias.parkourminigame.User;
import me.pafias.parkourminigame.Users;
import me.pafias.parkourminigame.game.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        User user = Users.getUser(event.getPlayer());
        if (user.isInGame())
            GameManager.leaveGame(user);
        Users.removeUser(event.getPlayer());
    }

}
