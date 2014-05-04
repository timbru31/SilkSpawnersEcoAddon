package de.dustplanet.silkspawnersecoaddon;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerChangeEvent;
import de.dustplanet.util.SilkUtil;

public class SilkSpawnersEcoAddonListener implements Listener {
    private SilkSpawnersEcoAddon plugin;
    private SilkUtil su;

    /**
     * This is the listener of the custom event to charge the user
     * 
     * @author xGhOsTkiLLeRx
     */

    public SilkSpawnersEcoAddonListener(SilkSpawnersEcoAddon instance) {
	plugin = instance;
	su = SilkUtil.hookIntoSilkSpanwers();
    }

    @EventHandler
    public void onSpawnerChange(SilkSpawnersSpawnerChangeEvent event) {
	// Get information
	Player player = event.getPlayer();
	short entityID = event.getEntityID();
	// Don't charge the same mob more than 1 time
	short spawnerID = event.getOldEntityID();
	if (!plugin.getConfig().getBoolean("chargeSameMob") && entityID == spawnerID) {
	    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.getString("sameMob")));
	    return;
	}

	// Hook into the pending confirmation list
	if (plugin.confirmation) {
	    UUID name = player.getUniqueId();
	    // Notify the player and cancel event
	    if (!plugin.pendingConfirmationList.contains(name)) {
		plugin.pendingConfirmationList.add(name);
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.getString("confirmationPending")));
		event.setCancelled(true);
	    } else {
		// Now remove the player and continue normal procedure
		plugin.pendingConfirmationList.remove(name);
	    }
	}

	// Get name and replace occurring spaces
	String name = su.getCreatureName(entityID).toLowerCase().replace(" ", "");
	double price = plugin.defaultPrice;
	// Is a specific price listed, yes get it!
	if (plugin.config.contains(name)) {
	    price = plugin.getConfig().getDouble(name);
	} else if (plugin.config.contains(Short.toString(entityID))) {
	    // Maybe only the ID is delivered?
	    price = plugin.config.getDouble(Short.toString(entityID));
	}
	// If price is 0 or player has free perm, stop here!
	if (price <= 0 || player.hasPermission("silkspawners.free")) {
	    return;
	}
	// If he has the money, charge it
	if (plugin.chargeXP) {
	    int totalXP = player.getTotalExperience();
	    if (totalXP >= price) {
		totalXP -= price;
		player.setTotalExperience(totalXP);
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.getString("afford")).replace("%money%", Double.toString(price)));
	    } else {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.getString("cantAfford")));
		event.setCancelled(true);
	    }
	} else {
	    if (plugin.economy.has(player.getName(), price)) {
		plugin.economy.withdrawPlayer(player.getName(), price);
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.getString("afford")).replace("%money%", Double.toString(price)));
	    } else {
		// Else notify and cancel
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.getString("cantAfford")));
		event.setCancelled(true);
	    }
	}
    }
}