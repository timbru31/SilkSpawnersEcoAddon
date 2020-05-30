package de.dustplanet.silkspawnersecoaddon.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import de.dustplanet.silkspawnersecoaddon.SilkSpawnersEcoAddon;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * The command executor for SilkSpawnersEcoAddon. Reloads the config.
 *
 * @author xGhOsTkiLLeRx
 */

public class SilkSpawnersEcoAddonCommandExecutor implements CommandExecutor {
    private SilkSpawnersEcoAddon plugin;

    @SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
    public SilkSpawnersEcoAddonCommandExecutor(SilkSpawnersEcoAddon instance) {
        plugin = instance;
    }

    @Override
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Default value is given and prevents a NPE")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) {
            return false;
        }
        if (args.length != 1 || !"reload".equalsIgnoreCase(args[0])) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("commandUsage", "")));
        } else {
            if (sender.hasPermission("silkspawners.reload") || sender instanceof ConsoleCommandSender) {
                plugin.reload();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("reloadSuccess", "")));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("noPermission", "")));
            }
        }
        return true;
    }
}
