package board;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;

public class ConfigManager {
    private final Plugin plugin;
    @SuppressWarnings("unused")
	private FileConfiguration config;
    private File killConfigFile;
    private YamlConfiguration killConfig;
    private Map<UUID, Integer> kills;
    private HolographicDisplaysAPI hologramsAPI;
    private Hologram hologram;
    private Location hologramLocation;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.kills = new HashMap<>();
        this.hologramsAPI = HolographicDisplaysAPI.get(plugin);
    }

    public void setupKillConfig() {
        killConfigFile = new File(plugin.getDataFolder(), "kills.yml");

        if (!killConfigFile.exists()) {
            killConfig = new YamlConfiguration();
        } else {
            killConfig = YamlConfiguration.loadConfiguration(killConfigFile);
            loadKillsFromConfig(); // Call the method to load kills data from the file
        }
        
        // Load the hologram location from the config file
        if (killConfig.contains("hologramLocation")) {
            hologramLocation = (Location) killConfig.get("hologramLocation");
        }
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
        
        // Save the hologram location to the config file
        if (hologramLocation != null) {
            killConfig.set("hologramLocation", hologramLocation);
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
        saveKillConfig();
    }
    
    public List<String> getTopPlayers() {
        List<String> topPlayers = new ArrayList<>();
        
        List<Map.Entry<UUID, Integer>> sortedKills = new ArrayList<>(kills.entrySet());
        sortedKills.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        
        for (int i = 0; i < 10; i++) {
            if (i < sortedKills.size()) {
                UUID playerUUID = sortedKills.get(i).getKey();
                int kills = sortedKills.get(i).getValue();
                Player player = Bukkit.getPlayer(playerUUID);
                String playerName = (player != null) ? player.getName() : "Unknown";
                topPlayers.add(playerName + ": " + kills);
            } else {
                topPlayers.add("");
            }
        }
        
        return topPlayers;
    }
    
    public void refreshLeaderboard() {
        if (hologram != null) {
            hologram.getLines().clear(); // Clear the existing lines
            
            List<String> topPlayers = getTopPlayers();
            
            hologram.getLines().appendText(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Leaderboard " + ChatColor.GRAY + "(Kills)");
            
            for (String playerLine : topPlayers) {
                hologram.getLines().appendText(playerLine);
            }
        }
    }

    public void setHologramLocation(Location location) {
        this.hologramLocation = location;
    }

    public Location getHologramLocation() {
        return hologramLocation;
    }
    
    public void createHologram(Location location) {
        this.hologram = hologramsAPI.createHologram(location);
    }

    public Hologram getHologram() {
        return hologram;
    }
}
