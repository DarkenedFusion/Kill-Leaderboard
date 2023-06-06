package board;

import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillEventListener implements Listener {
    private ConfigManager configManager;

    public KillEventListener(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player killer = ((LivingEntity) entity).getKiller();

        if (killer != null) {
            UUID playerUUID = killer.getUniqueId();
            configManager.incrementKills(playerUUID);
            configManager.saveKillConfig();
            killer.sendMessage("hi");
        }
    }

}
