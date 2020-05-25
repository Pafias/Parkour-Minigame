package me.pafias.parkourminigame.listeners;

import me.pafias.parkourminigame.ParkourMinigame;
import me.pafias.parkourminigame.User;
import me.pafias.parkourminigame.Users;
import me.pafias.parkourminigame.game.Game;
import me.pafias.parkourminigame.game.GameManager;
import me.pafias.parkourminigame.game.GameState;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class InGameListener implements Listener {

    int i = 5;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        User user = Users.getUser(event.getPlayer());
        if (user.isInGame()) {
            Game game = GameManager.getGame(user);
            if (game.getState() == GameState.INGAME) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                    switch (event.getClickedBlock().getType()) {
                        case REDSTONE_BLOCK:
                            event.getClickedBlock().breakNaturally();
                            for (User u : game.getPlayers())
                                if (u != user) {
                                    TNTPrimed tnt = u.getPlayer().getLocation().getWorld().spawn(u.getPlayer().getLocation().add(0, 2, 0), TNTPrimed.class);
                                    tnt.setFuseTicks(2 * 20);
                                    tnt.setYield(tnt.getYield() * (float) 1.5);
                                    tnt.setGlowing(true);
                                }
                            break;
                        case OBSIDIAN:
                            event.getClickedBlock().breakNaturally();
                            for (User u : game.getPlayers())
                                if (u != user)
                                    u.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (5 * 20), 1, false, false));
                            break;
                        case LAPIS_BLOCK:
                            event.getClickedBlock().breakNaturally();
                            user.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (16 * 20), 1, false, false));
                            user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1F, 1F);
                            break;
                        case IRON_BLOCK:
                            event.getClickedBlock().breakNaturally();
                            user.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, (16 * 20), 1, false, false));
                            user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1F, 1F);
                            break;
                        case GLASS:
                            event.getClickedBlock().breakNaturally();
                            user.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (16 * 20), 1, false, false));
                            user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1F, 1F);
                            break;
                        case DIAMOND_BLOCK:
                            event.getClickedBlock().breakNaturally();
                            user.setPoints(user.getPoints() + 1);
                            user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                            user.setTotalPoints(user.getTotalPoints() + 1);
                            if (user.getPoints() >= 10) {
                                game.broadcast("");
                                game.broadcast(ChatColor.GRAY + user.getPlayer().getName() + ChatColor.GOLD + " was the first to reach 10 points, and won the game!");
                                game.broadcast("");
                                for (User u : game.getPlayers())
                                    for (PotionEffect pe : u.getPlayer().getActivePotionEffects())
                                        u.getPlayer().removePotionEffect(pe.getType());
                                game.setGamestate(GameState.POSTGAME);
                                ParkourMinigame.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(ParkourMinigame.getInstance(),
                                        () -> {
                                            ParkourMinigame.getInstance().getServer().getScheduler().runTaskLater(ParkourMinigame.getInstance(),
                                                    () -> spawnFirework(user), 20);
                                            i = i - 1;
                                        }, 0, 20);
                                ParkourMinigame.getInstance().getServer().getScheduler().runTaskLater(ParkourMinigame.getInstance(), () -> game.stop(), (7 * 20));
                            }
                            break;
                    }
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.PRIMED_TNT)
            event.blockList().clear();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            User user = Users.getUser((Player) event.getEntity());
            if (user.isInGame()) {
                Game game = GameManager.getGame(user);
                if (game.getState() != GameState.INGAME)
                    event.setCancelled(true);
                event.setDamage(0);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        User user = Users.getUser(event.getPlayer());
        if (user.isInGame()) {
            Game game = GameManager.getGame(user);
            if (user.getPlayer().getLocation().getY() <= (game.getConfig().getConfig().getDouble("x-range-1") - 3))
                if (game.getState() == GameState.LOBBY || game.getState() == GameState.PREGAME)
                    user.getPlayer().teleport(game.getLobbySpawn());
                else
                    user.getPlayer().teleport(game.getSpawn());
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (Users.getUser(event.getPlayer()).isInGame())
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (Users.getUser(event.getPlayer()).isInGame())
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Users.getUser(event.getPlayer()).isInGame())
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Users.getUser(event.getPlayer()).isInGame())
            event.setCancelled(true);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player && Users.getUser((Player) event.getEntity()).isInGame())
            event.setCancelled(true);
    }


    private void spawnFirework(User user) {
        final Firework firework = (Firework) user.getPlayer().getLocation().getWorld()
                .spawnEntity(user.getPlayer().getLocation().add(0.5, 0.5, 0.5), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        Random r = new Random();
        int rt = r.nextInt(4) + 1;
        Type type = Type.BALL;
        if (rt == 1)
            type = Type.BALL;
        if (rt == 2)
            type = Type.BALL_LARGE;
        if (rt == 3)
            type = Type.BURST;
        if (rt == 4)
            type = Type.CREEPER;
        if (rt == 5)
            type = Type.STAR;
        int r1i = r.nextInt(17) + 1;
        int r2i = r.nextInt(17) + 1;
        Color c1 = getColor(r1i);
        Color c2 = getColor(r2i);
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type)
                .trail(r.nextBoolean()).build();
        meta.addEffect(effect);
        int rp = r.nextInt(2) + 1;
        meta.setPower(rp);
        firework.setFireworkMeta(meta);
        Bukkit.getScheduler().runTaskLater(ParkourMinigame.getInstance(), firework::detonate, 2);
    }

    private Color getColor(int i) {
        Color c = null;
        if (i == 1) {
            c = Color.AQUA;
        }
        if (i == 2) {
            c = Color.BLACK;
        }
        if (i == 3) {
            c = Color.BLUE;
        }
        if (i == 4) {
            c = Color.FUCHSIA;
        }
        if (i == 5) {
            c = Color.GRAY;
        }
        if (i == 6) {
            c = Color.GREEN;
        }
        if (i == 7) {
            c = Color.LIME;
        }
        if (i == 8) {
            c = Color.MAROON;
        }
        if (i == 9) {
            c = Color.NAVY;
        }
        if (i == 10) {
            c = Color.OLIVE;
        }
        if (i == 11) {
            c = Color.ORANGE;
        }
        if (i == 12) {
            c = Color.PURPLE;
        }
        if (i == 13) {
            c = Color.RED;
        }
        if (i == 14) {
            c = Color.SILVER;
        }
        if (i == 15) {
            c = Color.TEAL;
        }
        if (i == 16) {
            c = Color.WHITE;
        }
        if (i == 17) {
            c = Color.YELLOW;
        }

        return c;
    }
}
