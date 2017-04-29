package de.dustplanet.silkspawnersecoaddon.listeners;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerChangeEvent;
import de.dustplanet.silkspawnersecoaddon.SilkSpawnersEcoAddon;
import de.dustplanet.util.SilkUtil;

/**
 * This is the listener of the custom event to charge the user.
 *
 * @author xGhOsTkiLLeRx
 */

public class SilkSpawnersEcoSpawnerChangeListener implements Listener {
    private SilkSpawnersEcoAddon plugin;
    private SilkUtil su;

    public SilkSpawnersEcoSpawnerChangeListener(SilkSpawnersEcoAddon instance) {
        plugin = instance;
        su = SilkUtil.hookIntoSilkSpanwers();
    }

    @EventHandler
    public void onSpawnerChange(SilkSpawnersSpawnerChangeEvent event) {
        Player player = event.getPlayer();
        short entityID = event.getEntityID();
        short spawnerID = event.getOldEntityID();
        if (!plugin.getConfig().getBoolean("chargeSameMob") && entityID == spawnerID) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("sameMob")));
            return;
        }

        String name = su.getCreatureName(entityID).toLowerCase().replace(" ", "");
        double price = plugin.getDefaultPrice();

        if (plugin.getConfig().contains(name)) {
            price = plugin.getConfig().getDouble(name);
        } else if (plugin.getConfig().contains(Short.toString(entityID))) {
            price = plugin.getConfig().getDouble(Short.toString(entityID));
        }

        if (price <= 0 || player.hasPermission("silkspawners.free")) {
            return;
        }

        if (plugin.getConfig().getBoolean("chargeMultipleAmounts", false)) {
            price *= event.getAmount();
        }

        if (plugin.isConfirmation()) {
            UUID playerName = player.getUniqueId();
            if (!plugin.getPendingConfirmationList().contains(playerName)) {
                plugin.getPendingConfirmationList().add(playerName);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("confirmationPending")).replace("%money%", Double.toString(price)));
                event.setCancelled(true);
                return;
            }
            plugin.getPendingConfirmationList().remove(playerName);
        }

        int totalXP = player.getTotalExperience();
        
        if (plugin.isChargeXP()) {
            if (totalXP >= price) {
                totalXP -= price;
                player.setTotalExperience(0);
                player.setLevel(0);
                player.giveExp(totalXP);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("afford")).replace("%money%", Double.toString(price)));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("cantAffordXP")));
                event.setCancelled(true);
            }
        } else if (plugin.isChargeBoth()) {
            Boolean canAffordXP = totalXP >= price;
            Boolean canAffordMoney = plugin.getEcon().has(player, price);
            if (canAffordXP && canAffordMoney) {
                plugin.getEcon().withdrawPlayer(player, price);
                
                totalXP -= price;
                player.setTotalExperience(0);
                player.setLevel(0);
                player.giveExp(totalXP);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("afford")).replace("%money%", Double.toString(price)));
            } else {
                if (!canAffordXP)
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("cantAffordBothXP")));
                else
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("cantAffordBothMoney")));
                event.setCancelled(true);
            }
        } else {
            if (plugin.getEcon().has(player, price)) {
                plugin.getEcon().withdrawPlayer(player, price);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("afford")).replace("%money%", Double.toString(price)));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("cantAffordMoney")));
                event.setCancelled(true);
            }
        }
    }
}
