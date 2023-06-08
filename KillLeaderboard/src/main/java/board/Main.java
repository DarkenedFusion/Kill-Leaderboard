package board;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	private static Main instance;
	private ConfigManager configManager;

	@Override
	public void onEnable() {
		instance = this;

		// Simply checks for Holographics Display plugin and if not found disables
		// plugin
		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
			getLogger().severe("*** This plugin will be disabled. ***");
			this.setEnabled(false);
			return;
		}

		configManager = new ConfigManager(this);
	    configManager.setupKillConfig(); // Add this line to setup the killConfig
	    
	    configManager.saveKillConfig();
	   
		
		
	    Bukkit.getPluginManager().registerEvents(new KillEventListener(configManager), this);
	    Bukkit.getPluginManager().registerEvents(new ServerReloadListener(configManager), this);


	    
		getCommand("checkKills").setExecutor(new CheckKillCommand(configManager));
	    getCommand("createHologram").setExecutor(new CreateHologram(configManager));
	    getCommand("refreshLeaderboard").setExecutor(new RefreshLeaderboard(configManager));

	}

	@Override
	public void onDisable() {
	    configManager.saveKillConfig();

	}

	public static Main getInstance() {
		return instance;
	}
	
	

}