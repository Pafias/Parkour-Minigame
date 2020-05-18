package me.pafias.parkourminigame.listeners;

import me.pafias.parkourminigame.UserConfig;
import me.pafias.parkourminigame.Users;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        UserConfig uc = new UserConfig(event.getPlayer().getUniqueId());
        uc.createConfig(event.getPlayer());
        uc.getConfig().set("name", event.getPlayer().getName());
        uc.saveConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Users.addUser(event.getPlayer());
    }

}
