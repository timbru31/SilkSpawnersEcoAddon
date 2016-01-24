package de.dustplanet.silkspawnersecoaddon;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

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

    /**
     * The command executor for SilkSpawnersEcoAddon.
     * Reloads the config.
     *
     * @param sender the CommandSender
     * @param command the Command that was issued
     * @param label the CommandLabel that was used
     * @param args the arguments of the command
     * @return whether the command was successful
     */
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
