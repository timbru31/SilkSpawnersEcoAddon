package de.dustplanet.silkspawnersecoaddon.util;

import java.util.Locale;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.dustplanet.silkspawners.events.ISilkSpawnersEvent;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerChangeEvent;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerPlaceEvent;
import de.dustplanet.silkspawnersecoaddon.SilkSpawnersEcoAddon;
import de.dustplanet.util.SilkUtil;

public class SilkSpawnersEcoAddonUtil {
    private SilkSpawnersEcoAddon plugin;
    private SilkUtil su;

    public SilkSpawnersEcoAddonUtil(SilkSpawnersEcoAddon instance, SilkUtil silkUtil) {
        plugin = instance;
        su = silkUtil;
    }

    public boolean handleGenericEvent(ISilkSpawnersEvent event) {
        boolean isChangeEvent = event instanceof SilkSpawnersSpawnerChangeEvent;
        boolean isBreakEvent = event instanceof SilkSpawnersSpawnerBreakEvent;
        boolean isPlaceEvent = event instanceof SilkSpawnersSpawnerPlaceEvent;
        Player player = event.getPlayer();
        String entityID = event.getEntityID();
        String mode = null;
        if (isChangeEvent) {
            mode = ".change";
        } else if (isBreakEvent) {
            mode = ".break";
        } else if (isPlaceEvent) {
            mode = ".place";
        }

        if (isChangeEvent) {
            String spawnerID = ((SilkSpawnersSpawnerChangeEvent) event).getOldEntityID();
            if (!plugin.getConfig().getBoolean("chargeSameMob") && entityID.equalsIgnoreCase(spawnerID)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("sameMob")));
                return false;
            }
        }

        String name = su.getCreatureName(entityID).toLowerCase(Locale.ENGLISH).replace(" ", "");
        double priceMoney = plugin.getConfig().getDouble("default" + mode + ".money");
        double priceXP = plugin.getConfig().getDouble("default" + mode + ".xp");

        if (plugin.getConfig().contains(name)) {
            priceXP = plugin.getConfig().getDouble(name + mode + ".xp");
            priceMoney = plugin.getConfig().getDouble(name + mode + ".money");
        }

        if (priceXP == 0 && priceMoney == 0 || player.hasPermission("silkspawners.free")) {
            return false;
        }

        if (isChangeEvent && plugin.getConfig().getBoolean("chargeMultipleAmounts", false)) {
            priceXP *= ((SilkSpawnersSpawnerChangeEvent) event).getAmount();
            priceMoney *= ((SilkSpawnersSpawnerChangeEvent) event).getAmount();
        }

        if (plugin.isConfirmation()) {
            UUID playerName = player.getUniqueId();
            if (!plugin.getPendingConfirmationList().contains(playerName)) {
                plugin.getPendingConfirmationList().add(playerName);
                sendConfirmationMessage(player, priceXP, priceMoney);
                return true;
            }
            plugin.getPendingConfirmationList().remove(playerName);
        }

        int totalXP = player.getTotalExperience();

        if (plugin.isChargeBoth()) {
            return chargeBoth(player, priceXP, priceMoney, totalXP);
        } else if (plugin.isChargeXP()) {
            return chargeXP(player, priceXP, totalXP);
        } else {
            return chargeMoney(player, priceMoney);
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

    private boolean chargeMoney(Player player, double priceMoney) {
        if (plugin.getEcon().has(player, priceMoney)) {
            plugin.getEcon().withdrawPlayer(player, priceMoney);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("affordMoney"))
                    .replace("%money%", Double.toString(priceMoney)));
            return false;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordMoney")));
        return true;
    }

    private boolean chargeBoth(Player player, double priceXP, double priceMoney, int totalXP) {
        boolean canAffordXP = totalXP >= priceXP;
        boolean canAffordMoney = plugin.getEcon().has(player, priceMoney);
        if (canAffordXP && canAffordMoney) {
            plugin.getEcon().withdrawPlayer(player, priceMoney);

            totalXP -= priceXP;
            player.setTotalExperience(0);
            player.setLevel(0);
            player.giveExp(totalXP);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("affordBoth"))
                    .replace("%money%", Double.toString(priceMoney)).replace("%xp%", Double.toString(priceXP)));
            return false;
        }
        if (!canAffordXP) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordXP")));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordMoney")));
        }
        return true;
    }

    private boolean chargeXP(Player player, double priceXP, int totalXP) {
        if (totalXP >= priceXP) {
            totalXP -= priceXP;
            player.setTotalExperience(0);
            player.setLevel(0);
            player.giveExp(totalXP);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("affordXP")).replace("%xp%",
                    Double.toString(priceXP)));
            return false;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordXP")));
        return true;
    }
}
