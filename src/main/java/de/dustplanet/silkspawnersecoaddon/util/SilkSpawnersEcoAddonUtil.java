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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class SilkSpawnersEcoAddonUtil {
    private SilkSpawnersEcoAddon plugin;
    private SilkUtil su;

    @SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
    public SilkSpawnersEcoAddonUtil(SilkSpawnersEcoAddon instance, SilkUtil silkUtil) {
        plugin = instance;
        su = silkUtil;
    }

    @SuppressFBWarnings(value = { "BC_UNCONFIRMED_CAST",
            "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE" }, justification = "False positive and a default value is given and prevents a NPE")
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
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("sameMob", "")));
                return false;
            }
        }

        String name = su.getCreatureName(entityID).toLowerCase(Locale.ENGLISH).replace(" ", "");
        double priceMoney = plugin.getConfig().getDouble("default" + mode + ".money");
        int priceXP = plugin.getConfig().getInt("default" + mode + ".xp");

        if (plugin.getConfig().contains(name)) {
            priceXP = plugin.getConfig().getInt(name + mode + ".xp");
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

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Default value is given and prevents a NPE")
    private void sendConfirmationMessage(Player player, double priceXP, double priceMoney) {
        if (plugin.isChargeBoth()) {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("confirmationPendingBoth", ""))
                            .replace("%money%", Double.toString(priceMoney)).replace("%xp%", Double.toString(priceXP)));
        } else if (plugin.isChargeXP()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("confirmationPendingXP", ""))
                    .replace("%xp%", Double.toString(priceXP)));
        } else {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("confirmationPendingMoney", ""))
                            .replace("%money%", Double.toString(priceMoney)));
        }
    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Default value is given and prevents a NPE")
    private boolean chargeMoney(Player player, double priceMoney) {
        if (plugin.getEcon().has(player, priceMoney)) {
            plugin.getEcon().withdrawPlayer(player, priceMoney);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("affordMoney", ""))
                    .replace("%money%", Double.toString(priceMoney)));
            return false;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordMoney", "")));
        return true;
    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Default value is given and prevents a NPE")
    private boolean chargeBoth(Player player, int priceXP, double priceMoney, int totalXP) {
        boolean canAffordXP = totalXP >= priceXP;
        boolean canAffordMoney = plugin.getEcon().has(player, priceMoney);
        if (canAffordXP && canAffordMoney) {
            plugin.getEcon().withdrawPlayer(player, priceMoney);

            int newTotalXP = totalXP - priceXP;
            player.setTotalExperience(0);
            player.setLevel(0);
            player.giveExp(newTotalXP);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("affordBoth", ""))
                    .replace("%money%", Double.toString(priceMoney)).replace("%xp%", Double.toString(priceXP)));
            return false;
        }
        if (!canAffordXP) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordXP", "")));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordMoney", "")));
        }
        return true;
    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Default value is given and prevents a NPE")
    private boolean chargeXP(Player player, int priceXP, int totalXP) {
        if (totalXP >= priceXP) {
            int newTotalXP = totalXP - priceXP;
            player.setTotalExperience(0);
            player.setLevel(0);
            player.giveExp(newTotalXP);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("affordXP", ""))
                    .replace("%xp%", Double.toString(priceXP)));
            return false;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordXP", "")));
        return true;
    }
}
