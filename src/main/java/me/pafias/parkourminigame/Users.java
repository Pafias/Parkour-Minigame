package me.pafias.parkourminigame;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Users {

    private static Map<UUID, User> users = new HashMap<>();

    public static User getUser(Player player) {
        return users.get(player.getUniqueId());
    }

    public static User getUser(UUID uuid) {
        return users.get(uuid);
    }

    public static void addUser(Player player) {
        if (!users.containsKey(player.getUniqueId()))
            users.put(player.getUniqueId(), new User(player));
    }

    public static void removeUser(Player player) {
        users.remove(player.getUniqueId());
    }

    public static Map<UUID, User> getUsers() {
        return users;
    }

}
