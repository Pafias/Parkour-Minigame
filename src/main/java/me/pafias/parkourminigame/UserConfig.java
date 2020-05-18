package me.pafias.parkourminigame;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class UserConfig {

    UUID u;
    File File;
    FileConfiguration Config;

    public UserConfig(UUID u) {
        this.u = u;
        File = new File(ParkourMinigame.getInstance().getDataFolder() + "//playerdata//", u + ".yml");
        Config = YamlConfiguration.loadConfiguration(File);
    }

    public void createConfig(final Player player) {
        if (!(File.exists())) {
            try {
                YamlConfiguration UserConfig = YamlConfiguration.loadConfiguration(File);
                UserConfig.set("name", player.getName());
                UserConfig.set("totalPoints", 0);
                UserConfig.save(File);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration getConfig() {
        return Config;
    }

    public void saveConfig() {
        try {
            getConfig().save(File);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
