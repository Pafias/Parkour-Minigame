package me.pafias.parkourminigame.listeners;

import me.pafias.parkourminigame.User;
import me.pafias.parkourminigame.Users;
import me.pafias.parkourminigame.game.Game;
import me.pafias.parkourminigame.game.GameManager;
import me.pafias.parkourminigame.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PreGameListener implements Listener {

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            User damaged = Users.getUser((Player) event.getEntity());
            User damager = Users.getUser((Player) event.getDamager());
            if (damaged.isInGame() && damager.isInGame()) {
                Game game = GameManager.getGame(damaged);
                if (game.getState() != GameState.INGAME)
                    event.setCancelled(true);
            }
        }
    }

}
