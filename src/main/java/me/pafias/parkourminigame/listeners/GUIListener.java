package me.pafias.parkourminigame.listeners;

import me.pafias.parkourminigame.ParkourMinigame;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GUIListener implements Listener {

    private Map<String, Integer> map = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getInventory().getName().equals(ChatColor.GOLD + "Parkour Minigame Game Selection")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null && item.getType() != Material.AIR)
                player.performCommand("pm join " + event.getCurrentItem().getItemMeta().getLocalizedName());
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().getName().equalsIgnoreCase(ChatColor.GOLD + "Parkour Minigame Game Selection")) {
            if (!map.containsKey(event.getPlayer().getName()))
                map.put(event.getPlayer().getName(), ParkourMinigame.getInstance().getServer().getScheduler()
                        .scheduleAsyncRepeatingTask(ParkourMinigame.getInstance(), () -> ((Player) event.getPlayer()).updateInventory(), 2, 20));
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getName().equalsIgnoreCase(ChatColor.GOLD + "Parkour Minigame Game Selection") && map.containsKey(event.getPlayer().getName())) {
            ParkourMinigame.getInstance().getServer().getScheduler().cancelTask(map.get(event.getPlayer().getName()));
            map.remove(event.getPlayer().getName());
        }
    }

}
