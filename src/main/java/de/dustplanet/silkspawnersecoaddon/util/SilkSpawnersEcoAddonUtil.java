package de.dustplanet.silkspawnersecoaddon.util;

import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.dustplanet.silkspawners.events.ISilkSpawnersEvent;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerChangeEvent;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerPlaceEvent;
import de.dustplanet.silkspawnersecoaddon.SilkSpawnersEcoAddon;
import de.dustplanet.util.SilkUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Utility class to handle a generic break/place/change event.
 *
 * @author timbru31
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class SilkSpawnersEcoAddonUtil {
    private final SilkSpawnersEcoAddon plugin;
    private final SilkUtil silkUtil;

    @SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public SilkSpawnersEcoAddonUtil(final SilkSpawnersEcoAddon instance, final SilkUtil silkUtilInstance) {
        plugin = instance;
        this.silkUtil = silkUtilInstance;
    }

    @SuppressFBWarnings(value = { "BC_UNCONFIRMED_CAST",
            "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE" }, justification = "False positive and a default value is given and prevents a NPE")
    @SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.DataflowAnomalyAnalysis", "checkstyle:MissingJavadocMethod",
            "checkstyle:ReturnCount", "PMD.CyclomaticComplexity" })
    public boolean handleGenericEvent(final ISilkSpawnersEvent event) {
        final Player player = event.getPlayer();
        final String entityID = event.getEntityID();
        final boolean isChangeEvent = event instanceof SilkSpawnersSpawnerChangeEvent;

        if (isChangeEvent && abortBecauseOfSameMobInChangeEvent(player, event, entityID)) {
            return false;
        }

        final String mode = getModeFromEvent(event);
        final String name = silkUtil.getCreatureName(entityID).toLowerCase(Locale.ENGLISH).replace(" ", "");
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

        if (shouldAbortBecauseOfConfirmation(player, priceMoney, priceMoney)) {
            return true;
        }

        final int totalXP = player.getTotalExperience();

        return chargePlayer(player, priceMoney, priceXP, totalXP);
    }

    private boolean abortBecauseOfSameMobInChangeEvent(final Player player, final ISilkSpawnersEvent event, final String entityID) {
        final String spawnerID = ((SilkSpawnersSpawnerChangeEvent) event).getOldEntityID();
        if (!plugin.getConfig().getBoolean("chargeSameMob") && entityID.equalsIgnoreCase(spawnerID)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("sameMob", "")));
            return true;
        }
        return false;
    }

    @SuppressWarnings({ "static-method", "PMD.DataflowAnomalyAnalysis" })
    @Nullable
    private String getModeFromEvent(final ISilkSpawnersEvent event) {
        String mode = null;
        final boolean isChangeEvent = event instanceof SilkSpawnersSpawnerChangeEvent;
        final boolean isBreakEvent = event instanceof SilkSpawnersSpawnerBreakEvent;
        final boolean isPlaceEvent = event instanceof SilkSpawnersSpawnerPlaceEvent;
        if (isChangeEvent) {
            mode = ".change";
        } else if (isBreakEvent) {
            mode = ".break";
        } else if (isPlaceEvent) {
            mode = ".place";
        }
        return mode;
    }

    private boolean shouldAbortBecauseOfConfirmation(final Player player, final double priceXP, final double priceMoney) {
        if (plugin.isConfirmation()) {
            final UUID playerName = player.getUniqueId();
            if (!plugin.getPendingConfirmationList().contains(playerName)) {
                plugin.getPendingConfirmationList().add(playerName);
                sendConfirmationMessage(player, priceXP, priceMoney);
                return true;
            }
            plugin.getPendingConfirmationList().remove(playerName);
        }
        return false;
    }

    private boolean chargePlayer(final Player player, final double priceMoney, final int priceXP, final int totalXP) {
        if (plugin.isChargeBoth()) {
            return chargeBoth(player, priceXP, priceMoney, totalXP);
        } else if (plugin.isChargeXP()) {
            return chargeXP(player, priceXP, totalXP);
        } else {
            return chargeMoney(player, priceMoney);
        }
    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Default value is given and prevents a NPE")
    @SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "checkstyle:SeparatorWrap" })
    private void sendConfirmationMessage(final Player player, final double priceXP, final double priceMoney) {
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
    @SuppressWarnings("checkstyle:SeparatorWrap")
    private boolean chargeMoney(final Player player, final double priceMoney) {
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
    @SuppressWarnings("checkstyle:SeparatorWrap")
    private boolean chargeBoth(final Player player, final int priceXP, final double priceMoney, final int totalXP) {
        final boolean canAffordXP = totalXP >= priceXP;
        final boolean canAffordMoney = plugin.getEcon().has(player, priceMoney);
        if (canAffordXP && canAffordMoney) {
            plugin.getEcon().withdrawPlayer(player, priceMoney);

            final int newTotalXP = totalXP - priceXP;
            player.setTotalExperience(0);
            player.setLevel(0);
            player.giveExp(newTotalXP);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("affordBoth", ""))
                    .replace("%money%", Double.toString(priceMoney)).replace("%xp%", Double.toString(priceXP)));
            return false;
        }
        if (canAffordXP) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordMoney", "")));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLocalization().getString("cantAffordXP", "")));
        }
        return true;
    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Default value is given and prevents a NPE")
    @SuppressWarnings("checkstyle:SeparatorWrap")
    private boolean chargeXP(final Player player, final int priceXP, final int totalXP) {
        if (totalXP >= priceXP) {
            final int newTotalXP = totalXP - priceXP;
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
