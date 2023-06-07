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
	private Map<UUID, String> playerNames;
	private Location hologramLocation;
	private Hologram hologram;

	public ConfigManager(Plugin plugin) {
		this.plugin = plugin;
		this.kills = new HashMap<>();
		this.playerNames = new HashMap<>();
	}

	public void setupKillConfig() {
		killConfigFile = new File(plugin.getDataFolder(), "kills.yml");

		if (!killConfigFile.exists()) {
			killConfig = new YamlConfiguration();
		} else {
			killConfig = YamlConfiguration.loadConfiguration(killConfigFile);
			loadKillsFromConfig();
		}
	}

	private void loadKillsFromConfig() {
		if (killConfig.isConfigurationSection("kills")) {
			for (String key : killConfig.getConfigurationSection("kills").getKeys(false)) {
				UUID playerUUID = UUID.fromString(key);
				int kills = killConfig.getInt("kills." + key);
				String playerName = killConfig.getString("playerNames." + key);
				this.kills.put(playerUUID, kills);
				this.playerNames.put(playerUUID, playerName);
			}
		}
	}

	public void saveKillConfig() {
		for (Map.Entry<UUID, Integer> entry : kills.entrySet()) {
			killConfig.set("kills." + entry.getKey().toString(), entry.getValue());
			killConfig.set("playerNames." + entry.getKey().toString(), getPlayerName(entry.getKey()));
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
		playerNames.put(playerUUID, Bukkit.getPlayer(playerUUID).getName());
		saveKillConfig();
	}

	public List<String> getTopPlayers() {
		List<String> topPlayers = new ArrayList<>();

		List<Map.Entry<UUID, Integer>> sortedKills = new ArrayList<>(kills.entrySet());
		sortedKills.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

		for (int i = 1; i < 11; i++) {
			if (i < sortedKills.size()) {
				UUID playerUUID = sortedKills.get(i).getKey();
				int kills = sortedKills.get(i).getValue();
				String playerName = getPlayerName(playerUUID);
				topPlayers.add
				(ChatColor.GRAY +  "#" + ChatColor.GRAY + i + " " + ChatColor.RED + "" + 
				playerName + ChatColor.GRAY + "" + ChatColor.ITALIC + " (" + kills + ")");
			} else {
				topPlayers.add("");
			}
		}

		return topPlayers;
	}

	private String getPlayerName(UUID playerUUID) {
		String playerName = playerNames.get(playerUUID);
		if (playerName == null) {
			Player player = Bukkit.getPlayer(playerUUID);
			if (player != null) {
				playerName = player.getName();
				playerNames.put(playerUUID, playerName);
			}
		}
		return playerName;
	}

	public void createHologram(Location location) {
		hologramLocation = location;

		if (hologram != null) {
			hologram.delete(); // Delete the existing hologram if it exists
		}

		hologram = HolographicDisplaysAPI.get(plugin).createHologram(location);
		List<String> topPlayers = getTopPlayers();

		hologram.getLines().clear();
		hologram.getLines().appendText(ChatColor.RED + "" + ChatColor.BOLD + "LEADERBOARD " + ChatColor.GRAY + ""
				+ ChatColor.ITALIC + "(Kills)");

		for (String playerLine : topPlayers) {
			hologram.getLines().appendText(playerLine);
		}
	}

	public void refreshLeaderboard() {
		if (hologram != null) {
			List<String> topPlayers = getTopPlayers();

			hologram.getLines().clear();
			hologram.getLines().appendText(ChatColor.RED + "" + ChatColor.BOLD + "LEADERBOARD " + ChatColor.GRAY + ""
					+ ChatColor.ITALIC + "(Kills)");

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
}
