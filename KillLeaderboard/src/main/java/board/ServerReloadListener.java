package board;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class ServerReloadListener implements Listener {

    private final ConfigManager configManager;

    public ServerReloadListener(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @EventHandler
    public void onServerReload(ServerCommandEvent event) {
        if (event.getCommand().equalsIgnoreCase("reload") && event.getSender().isOp()) {
            if (configManager.getHologramLocation() != null) {
                configManager.refreshLeaderboard();
            } else {
                configManager.createHologram(configManager.getHologramLocation());
            }
        }
    }
}
