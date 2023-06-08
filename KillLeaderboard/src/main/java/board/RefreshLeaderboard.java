package board;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RefreshLeaderboard implements CommandExecutor {

	private final ConfigManager configManager;

	public RefreshLeaderboard(ConfigManager configManager) {
		this.configManager = configManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("refreshleaderboard")) {
			if (sender.isOp()) {
				if (configManager.getHologramLocation() != null) {
					configManager.refreshLeaderboard(); // Call the method to refresh the leaderboard
					sender.sendMessage("Leaderboard refreshed successfully.");
				} else {
					sender.sendMessage("There is no leaderboard to refresh.");
				}
				return true;
			}
		}
		return false;
	}
}