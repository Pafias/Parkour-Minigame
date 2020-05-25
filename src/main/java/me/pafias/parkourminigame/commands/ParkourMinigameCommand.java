package me.pafias.parkourminigame.commands;

import me.pafias.parkourminigame.User;
import me.pafias.parkourminigame.UserConfig;
import me.pafias.parkourminigame.Users;
import me.pafias.parkourminigame.game.Game;
import me.pafias.parkourminigame.game.GameManager;
import me.pafias.parkourminigame.game.GameState;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ParkourMinigameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "------------ Parkour Minigame ------------");
            sender.sendMessage(ChatColor.DARK_AQUA + "/pm create <world>");
            sender.sendMessage(ChatColor.DARK_AQUA + "/pm stats [player]");
            sender.sendMessage(ChatColor.DARK_AQUA + "/pm games");
            sender.sendMessage(ChatColor.DARK_AQUA + "/pm join <game id>");
            sender.sendMessage(ChatColor.DARK_AQUA + "/pm stop [game id]");
            sender.sendMessage(ChatColor.DARK_AQUA + "/pm forcestart");
            sender.sendMessage(ChatColor.DARK_AQUA + "/pm leave");
            sender.sendMessage(ChatColor.DARK_AQUA + "/pm gui");
            sender.sendMessage(ChatColor.GOLD + "-------------------------------------------");
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("stats")) {
                User user = Users.getUser((Player) sender);
                user.getPlayer().sendMessage(ChatColor.GOLD + "------------ Parkour Minigame ------------");
                user.getPlayer().sendMessage(ChatColor.GOLD + "Your total points: " + ChatColor.LIGHT_PURPLE + user.getTotalPoints());
                user.getPlayer().sendMessage(ChatColor.GOLD + "------------------------------------------");
            } else if (args[0].equalsIgnoreCase("games")) {
                if (GameManager.getGames().values().isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "No games available at the moment.");
                    return true;
                }
                sender.sendMessage(ChatColor.GOLD + "Join a game by clicking on the game you want below or by using /pm join <ID>");
                for (Game game : GameManager.getGames().values())
                    sender.sendMessage(new ComponentBuilder(ChatColor.GOLD + "ID: " + ChatColor.AQUA + game.getID() + ChatColor.GOLD + " Map: " + ChatColor.DARK_PURPLE + ChatColor.translateAlternateColorCodes('&', game.getName())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pm join " + game.getID())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.WHITE + "/pm join " + game.getID()).create())).create());
            } else if (args[0].equalsIgnoreCase("forcestart") && sender.isOp()) {
                User user = Users.getUser((Player) sender);
                if (!user.isInGame())
                    sender.sendMessage(ChatColor.RED + "You are not in a game.");
                else {
                    Game game = GameManager.getGame(Users.getUser((Player) sender));
                    game.start(true);
                    sender.sendMessage(ChatColor.GREEN + "You force started the game.");
                }
            } else if (args[0].equalsIgnoreCase("stop") && sender.isOp()) {
                User user = Users.getUser((Player) sender);
                if (!user.isInGame())
                    sender.sendMessage(ChatColor.RED + "You are not in a game.");
                else {
                    Game game = GameManager.getGame(user);
                    game.stop();
                    sender.sendMessage(ChatColor.GOLD + "Game stopped.");
                }
            } else if (args[0].equalsIgnoreCase("leave")) {
                User user = Users.getUser((Player) sender);
                if (user.isInGame())
                    GameManager.leaveGame(user);
                else
                    sender.sendMessage(ChatColor.RED + "You are not in a game!");
            } else if (args[0].equalsIgnoreCase("gui")) {
                User user = Users.getUser((Player) sender);
                if (GameManager.getGames().isEmpty()) {
                    user.getPlayer()
                            .sendMessage(ChatColor.RED + "There are currently no games available to join.");
                    return true;
                }
                int size = GameManager.getGames().size() <= 9 ? 9 : GameManager.getGames().size() <= 18 ? 18 : GameManager.getGames().size() <= 27 ? 27 : 54;
                Inventory inv = Bukkit.createInventory(null, size, ChatColor.GOLD + "Parkour Minigame Game Selection");
                for (Game game : GameManager.getGames().values()) {
                    if (game.getState().equals(GameState.LOBBY) || game.getState().equals(GameState.PREGAME)) {
                        ItemStack is = new ItemStack(Material.WOOL, 1, DyeColor.LIME.getWoolData());
                        ItemMeta meta = is.getItemMeta();
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', game.getName()));
                        meta.setLore(Arrays.asList("",
                                ChatColor.GOLD + "Players: " + ChatColor.GRAY + game.getPlayers().size()
                                        + ChatColor.GOLD + "/" + ChatColor.GRAY + game.getMaxPlayers(),
                                "", ChatColor.GREEN + "Click here to join this game!", ""));
                        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        meta.setLocalizedName(game.getID());
                        is.setItemMeta(meta);
                        inv.addItem(is);
                    }
                }
                user.getPlayer().openInventory(inv);
            }
            return true;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create") && sender.isOp()) {
                String game = GameManager.createGame(args[1]);
                sender.sendMessage(ChatColor.GREEN + "Game with ID " + ChatColor.LIGHT_PURPLE + game + ChatColor.GREEN + " created!");
            } else if (args[0].equalsIgnoreCase("stats")) {
                if (Bukkit.getPlayer(args[1]) != null) {
                    User user = Users.getUser(Bukkit.getPlayer(args[1]));
                    sender.sendMessage(ChatColor.GOLD + "------------ Parkour Minigame ------------");
                    sender.sendMessage(ChatColor.GRAY + user.getPlayer().getName() + ChatColor.GOLD + (user.getPlayer().getName().endsWith("s") ? "'" : "'s") + " total points: " + ChatColor.LIGHT_PURPLE + user.getTotalPoints());
                    sender.sendMessage(ChatColor.GOLD + "------------------------------------------");
                } else {
                    UserConfig config = new UserConfig(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                    sender.sendMessage(ChatColor.GRAY + config.getConfig().getString("name") + ChatColor.GOLD + (config.getConfig().getString("name").endsWith("s") ? "'" : "'s") + " total points: " + ChatColor.LIGHT_PURPLE + config.getConfig().getInt("totalPoints"));
                }
            } else if (args[0].equalsIgnoreCase("join")) {
                User user = Users.getUser((Player) sender);
                if (GameManager.getGames().containsKey(args[1]))
                    if (!user.isInGame())
                        GameManager.joinGame(Users.getUser((Player) sender), args[1]);
                    else
                        sender.sendMessage(ChatColor.RED + "You are already in a game!");
                else
                    sender.sendMessage(ChatColor.RED + "Game not found");
            } else if (args[0].equalsIgnoreCase("stop") && sender.isOp()) {
                if (!GameManager.getGames().containsKey(args[1])) {
                    sender.sendMessage(ChatColor.RED + "Game not found!");
                    return true;
                }
                Game game = GameManager.getGames().get(args[1]);
                game.stop();
                sender.sendMessage(ChatColor.GOLD + "Game stopped.");
            }
            return true;
        }
        return true;
    }

}
