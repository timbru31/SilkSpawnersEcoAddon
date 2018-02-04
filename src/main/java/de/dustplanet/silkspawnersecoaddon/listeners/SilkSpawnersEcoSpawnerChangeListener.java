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
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("sameMob")));
            return;
        }

        String name = su.getCreatureName(entityID).toLowerCase().replace(" ", "");
        double priceMoney = plugin.getDefaultPriceMoney();
        double priceXP = plugin.getDefaultPriceXP();

        if (plugin.getConfig().contains(name)) {
            priceXP = plugin.getConfig().getDouble(name + ".xp");
            priceMoney = plugin.getConfig().getDouble(name + ".money");
        } else if (plugin.getConfig().contains(Short.toString(entityID))) {
            priceXP = plugin.getConfig().getDouble(Short.toString(entityID) + ".xp");
            priceMoney = plugin.getConfig().getDouble(Short.toString(entityID) + ".money");
        }

        if ((priceXP == 0 && priceMoney == 0) || player.hasPermission("silkspawners.free")) {
            return;
        }

        if (plugin.getConfig().getBoolean("chargeMultipleAmounts", false)) {
            priceXP *= event.getAmount();
            priceMoney *= event.getAmount();
        }

        if (plugin.isConfirmation()) {
            UUID playerName = player.getUniqueId();
            if (!plugin.getPendingConfirmationList().contains(playerName)) {
                plugin.getPendingConfirmationList().add(playerName);
                sendConfirmationMessage(player, priceXP, priceMoney);
                event.setCancelled(true);
                return;
            }
            plugin.getPendingConfirmationList().remove(playerName);
        }

        int totalXP = player.getTotalExperience();

        if (plugin.isChargeBoth()) {
            chargeBoth(event, player, priceXP, priceMoney, totalXP);
        } else if (plugin.isChargeXP()) {
            chargeXP(event, player, priceXP, totalXP);
        } else {
            chargeMoney(event, player, priceMoney);
        }
    }

    private void sendConfirmationMessage(Player player, double priceXP, double priceMoney) {
        if (plugin.isChargeBoth()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("confirmationPendingBoth"))
                    .replace("%money%", Double.toString(priceMoney)).replace("%xp%", Double.toString(priceXP)));
        } else if (plugin.isChargeXP()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("confirmationPendingXP"))
                    .replace("%xp%", Double.toString(priceXP)));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("confirmationPendingMoney"))
                    .replace("%money%", Double.toString(priceMoney)));
        }
    }

    private void chargeMoney(SilkSpawnersSpawnerChangeEvent event, Player player, double priceMoney) {
        if (plugin.getEcon().has(player, priceMoney)) {
            plugin.getEcon().withdrawPlayer(player, priceMoney);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("affordMoney"))
                    .replace("%money%", Double.toString(priceMoney)));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordMoney")));
            event.setCancelled(true);
        }
    }

    private void chargeBoth(SilkSpawnersSpawnerChangeEvent event, Player player, double priceXP, double priceMoney, int totalXP) {
        boolean canAffordXP = totalXP >= priceXP;
        boolean canAffordMoney = plugin.getEcon().has(player, priceMoney);
        if (canAffordXP && canAffordMoney) {
            plugin.getEcon().withdrawPlayer(player, priceMoney);

            totalXP -= priceXP;
            player.setTotalExperience(0);
            player.setLevel(0);
            player.giveExp(totalXP);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("affordBoth"))
                    .replace("%money%", Double.toString(priceMoney).replace("%xp%", Double.toString(priceXP))));
        } else {
            if (!canAffordXP) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordXP")));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordMoney")));
            }
            event.setCancelled(true);
        }
    }

    private void chargeXP(SilkSpawnersSpawnerChangeEvent event, Player player, double priceXP, int totalXP) {
        if (totalXP >= priceXP) {
            totalXP -= priceXP;
            player.setTotalExperience(0);
            player.setLevel(0);
            player.giveExp(totalXP);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("affordXP")).replace("%xp%",
                    Double.toString(priceXP)));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordXP")));
            event.setCancelled(true);
        }
    }
}
