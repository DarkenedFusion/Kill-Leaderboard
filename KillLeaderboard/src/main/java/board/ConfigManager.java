package board;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
    private final Plugin plugin;
    private FileConfiguration config;
    private File configFile;
    private File killConfigFile;
    private YamlConfiguration killConfig;
    private Map<UUID, Integer> kills;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.kills = new HashMap<>();
    }

    public void setupConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void setupKillConfig() {
        killConfigFile = new File(plugin.getDataFolder(), "kills.yml");
        if (!killConfigFile.exists()) {
            try {
                killConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        killConfig = YamlConfiguration.loadConfiguration(killConfigFile);
        loadKillsFromConfig();
    }

    private void loadKillsFromConfig() {
        if (killConfig.isConfigurationSection("kills")) {
            for (String key : killConfig.getConfigurationSection("kills").getKeys(false)) {
                UUID playerUUID = UUID.fromString(key);
                int kills = killConfig.getInt("kills." + key);
                this.kills.put(playerUUID, kills);
            }
        }
    }

    public void saveKillConfig() {
        for (Map.Entry<UUID, Integer> entry : kills.entrySet()) {
            killConfig.set("kills." + entry.getKey().toString(), entry.getValue());
        }
        try {
            killConfig.save(killConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getKills(UUID playerUUID) {
        return kills.getOrDefault(playerUUID, 0);
    }

    public void incrementKills(UUID playerUUID) {
        int currentKills = getKills(playerUUID);
        kills.put(playerUUID, currentKills + 1);
    }
}
