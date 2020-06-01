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
 * @author timbru31
 */

public class SilkSpawnersEcoAddonCommandExecutor implements CommandExecutor {
    private static final String RELOAD_ARGUMENT = "reload";
    private final SilkSpawnersEcoAddon plugin;

    @SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public SilkSpawnersEcoAddonCommandExecutor(final SilkSpawnersEcoAddon instance) {
        plugin = instance;
    }

    @Override
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Default value is given and prevents a NPE")
    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender == null) {
            return false;
        }

        if (args.length == 1 && RELOAD_ARGUMENT.equalsIgnoreCase(args[0])) {
            if (sender.hasPermission("silkspawners.reload") || sender instanceof ConsoleCommandSender) {
                plugin.reload();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("reloadSuccess", "")));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("noPermission", "")));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("commandUsage", "")));
        }
        return true;
    }
}
