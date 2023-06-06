package board;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CreateHologram implements CommandExecutor {

    private final ConfigManager configManager;

    public CreateHologram(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("createhologram")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;

            Location hologramLocation = player.getLocation();

            // Create a new hologram at the player's location
            configManager.createHologram(hologramLocation);
            configManager.setHologramLocation(hologramLocation);

            configManager.refreshLeaderboard();

            player.sendMessage("Hologram created successfully!");

            return true;
        }
        return false;
    }
}
