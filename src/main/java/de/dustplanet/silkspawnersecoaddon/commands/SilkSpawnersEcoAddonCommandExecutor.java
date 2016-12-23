package de.dustplanet.silkspawnersecoaddon.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import de.dustplanet.silkspawnersecoaddon.SilkSpawnersEcoAddon;

/**
 * The command executor for SilkSpawnersEcoAddon.
 * Reloads the config.
 *
 * @author xGhOsTkiLLeRx
*/

public class SilkSpawnersEcoAddonCommandExecutor implements CommandExecutor {
    /**
     * SilkSpawnersEcoAddon instance.
     */
    private SilkSpawnersEcoAddon plugin;

    /**
     * Creates a new SilkSpawnersEcoAddonCommandExecutor.
     *
     * @param instance of SilkSpawnersEcoAddon
     */
    public SilkSpawnersEcoAddonCommandExecutor(SilkSpawnersEcoAddon instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("commandUsage")));
        } else {
            if (sender.hasPermission("silkspawners.reload") || sender instanceof ConsoleCommandSender) {
                plugin.reload();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("reloadSuccess")));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("noPermission")));
            }
        }
        return true;
    }
}
