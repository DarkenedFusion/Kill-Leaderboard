package board;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CheckKillCommand implements CommandExecutor, Listener {

    private final ConfigManager configManager;

    public CheckKillCommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("checkKills") && sender.isOp()) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console can not use this command");
                return true;
            }
            Player player = (Player) sender;
            int kills = configManager.getKills(player.getUniqueId());

            player.sendMessage(player.getName() + " has " + kills + " total kills!");

            return true;
        }
        return false;
    }

}