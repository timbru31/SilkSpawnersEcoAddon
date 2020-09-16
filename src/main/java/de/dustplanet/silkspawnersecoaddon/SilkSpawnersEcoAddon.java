package de.dustplanet.silkspawnersecoaddon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bstats.bukkit.Metrics;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.dustplanet.silkspawnersecoaddon.commands.SilkSpawnersEcoAddonCommandExecutor;
import de.dustplanet.silkspawnersecoaddon.listeners.SilkSpawnersEcoAddonSpawnerBreakListener;
import de.dustplanet.silkspawnersecoaddon.listeners.SilkSpawnersEcoAddonSpawnerChangeListener;
import de.dustplanet.silkspawnersecoaddon.listeners.SilkSpawnersEcoAddonSpawnerPlaceListener;
import de.dustplanet.silkspawnersecoaddon.util.ScalarYamlConfiguration;
import de.dustplanet.util.SilkUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.Setter;
import net.gravitydevelopment.updater.Updater;
import net.milkbowl.vault.economy.Economy;

/**
 * General loading of the plugin, config and localization files.
 *
 * @author timbru31
 */
@SuppressFBWarnings({ "FCCD_FIND_CLASS_CIRCULAR_DEPENDENCY", "CD_CIRCULAR_DEPENDENCY" })
@SuppressWarnings({ "checkstyle:MultipleStringLiterals", "checkstyle:MissingCtor", "PMD.AtLeastOneConstructor" })
public class SilkSpawnersEcoAddon extends JavaPlugin {
    private static final int BUFFER_SIZE = 1024;
    private static final int BSTATS_PLUGIN_ID = 550;
    private static final long TICKS_PER_SECOND = 20L;
    private static final int PLUGIN_ID = 52_026;
    @Getter
    @Setter
    private FileConfiguration localization;
    private File localizationFile;
    @Getter
    @Setter
    private Economy econ;
    @Getter
    @Setter
    private boolean chargeXP;
    @Getter
    @Setter
    private boolean chargeBoth;
    @Getter
    @Setter
    private boolean confirmation;
    @Getter
    @Setter
    private List<UUID> pendingConfirmationList = new ArrayList<>();
    @Getter
    @Setter
    private SilkUtil silkUtil;
    @Getter
    @Setter
    private DecimalFormat numberFormat;

    @Override
    public void onDisable() {
        disable();
    }

    @SuppressWarnings("unused")
    @Override
    @SuppressFBWarnings("SEC_SIDE_EFFECT_CONSTRUCTOR")
    public void onEnable() {
        setSilkUtil(SilkUtil.hookIntoSilkSpanwers());

        final File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            if (configFile.getParentFile().mkdirs()) {
                copy("config.yml", configFile);
            } else {
                getLogger().severe("The config folder could NOT be created, make sure it's writable!");
                getLogger().severe("Disabling now!");
                setEnabled(false);
                return;
            }
        }

        loadConfig();

        checkForUpdate();

        localizationFile = new File(getDataFolder(), "localization.yml");
        if (!localizationFile.exists()) {
            copy("localization.yml", localizationFile);
        }

        setLocalization(ScalarYamlConfiguration.loadConfiguration(localizationFile));
        loadLocalization();

        final PluginCommand command = getCommand("silkspawnerseco");
        if (command != null) {
            command.setExecutor(new SilkSpawnersEcoAddonCommandExecutor(this));
        }

        if (setupEconomy()) {
            getLogger().info("Loaded Vault successfully");
        } else {
            getLogger().severe("Vault was not found! XP charging is now ON...");
            setChargeXP(true);
        }

        getServer().getPluginManager().registerEvents(new SilkSpawnersEcoAddonSpawnerChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new SilkSpawnersEcoAddonSpawnerBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new SilkSpawnersEcoAddonSpawnerPlaceListener(this), this);

        new Metrics(this, BSTATS_PLUGIN_ID);

        registerTask();
    }

    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "checkstyle:IllegalCatch" })
    private void checkForUpdate() {
        if (getConfig().getBoolean("autoUpdater", true)) {
            if (getDescription().getVersion().contains("SNAPSHOT")) {
                getLogger().info("AutoUpdater is disabled because you are running a dev build!");
            } else {
                try {
                    final Updater updater = new Updater(this, PLUGIN_ID, getFile(), Updater.UpdateType.DEFAULT, true);
                    getLogger().info("AutoUpdater is enabled.");
                    getLogger().info("Result from AutoUpdater is: " + updater.getResult().name());
                } catch (final Exception e) {
                    getLogger().info("Error while auto updating: " + e.getMessage().replaceAll("[\r\n]", ""));
                }
            }
        } else {
            getLogger().info("AutoUpdater is disabled due to config setting.");
        }
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void reload() {
        disable();
        this.reloadConfig();
        final FileConfiguration config = getConfig();
        setConfirmation(config.getBoolean("confirmation.enabled"));
        registerTask();
    }

    @SuppressFBWarnings({ "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE",
            "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE" })
    @SuppressWarnings({ "PMD.AssignmentInOperand", "PMD.DataflowAnomalyAnalysis" })
    private void copy(final String yml, final File file) {
        try (OutputStream out = Files.newOutputStream(file.toPath()); InputStream inputStream = getResource(yml)) {
            final byte[] buf = new byte[BUFFER_SIZE];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (final IOException e) {
            getLogger().log(Level.WARNING, "Failed to copy the default config! (I/O)", e);
        }
    }

    private void disable() {
        getServer().getScheduler().cancelTasks(this);
        getPendingConfirmationList().clear();
    }

    @SuppressWarnings({ "checkstyle:ExecutableStatementCount", "checkstyle:MagicNumber" })
    private void loadConfig() {
        final FileConfiguration config = getConfig();
        config.options().header("You can configure every mob name (without spaces) or a default!");
        config.addDefault("autoUpdater", Boolean.TRUE);
        config.addDefault("chargeSameMob", Boolean.FALSE);
        config.addDefault("chargeXP", Boolean.FALSE);
        config.addDefault("chargeBoth", Boolean.FALSE);
        config.addDefault("chargeMultipleAmounts", Boolean.FALSE);
        config.addDefault("numberFormat", "$ 00.##");
        config.addDefault("confirmation.enabled", Boolean.FALSE);
        config.addDefault("confirmation.delay", 30);
        config.addDefault("default.break.money", 10.5);
        config.addDefault("default.break.xp", 100);
        config.addDefault("default.change.money", 10.5);
        config.addDefault("default.change.xp", 100);
        config.addDefault("default.place.money", 10.5);
        config.addDefault("default.place.xp", 100);
        config.addDefault("pig.break.money", 7.25);
        config.addDefault("pig.break.xp", 200);
        config.addDefault("pig.change.money", 7.25);
        config.addDefault("pig.change.xp", 200);
        config.addDefault("pig.place.money", 7.25);
        config.addDefault("pig.place.xp", 200);
        config.addDefault("cow.break.money", 0.00);
        config.addDefault("cow.break.xp", 20);
        config.addDefault("cow.change.money", 0.00);
        config.addDefault("cow.change.xp", 20);
        config.addDefault("cow.place.money", 0.00);
        config.addDefault("cow.place.xp", 20);
        config.options().copyDefaults(true);
        saveConfig();

        setChargeXP(config.getBoolean("chargeXP"));
        setChargeBoth(config.getBoolean("chargeBoth"));
        setConfirmation(config.getBoolean("confirmation.enabled"));
        final String numberFormatString = config.getString("numberFormat", "$ 00.##");
        setNumberFormat(new DecimalFormat(numberFormatString));
    }

    @SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", justification = "onEnable is our \"constructor\"")
    @SuppressWarnings("checkstyle:LineLength")
    private void loadLocalization() {
        localization.addDefault("cantAffordMoney",
                "&e[SilkSpawnersEco] &4Sorry, but you can't do this action, because you have not enough money!");
        localization.addDefault("cantAffordXP",
                "&e[SilkSpawnersEco] &4Sorry, but you can't do this action, because you have not enough XP!");
        localization.addDefault("affordBoth", "&e[SilkSpawnersEco] &2This action costs &e%money% &2and &e%xp% &2XP");
        localization.addDefault("affordMoney", "&e[SilkSpawnersEco] &2This action costs &e%money%");
        localization.addDefault("affordXP", "&e[SilkSpawnersEco] &2This action costs &e%xp%");
        localization.addDefault("sameMob", "&e[SilkSpawnersEco] &2This action was free, because it's the same mob!");
        localization.addDefault("confirmationPendingBoth",
                "&e[SilkSpawnersEco] Remember that this action costs &2%money%&e and &2%xp% &eXP, if you want to continue, do the action again!");
        localization.addDefault("confirmationPendingMoney",
                "&e[SilkSpawnersEco] Remember that this action costs &2%money%&e, if you want to continue, do the action again!");
        localization.addDefault("confirmationPendingXP",
                "&e[SilkSpawnersEco] Remember that this action costs &2%xp%&e XP, if you want to continue, do the action again!");
        localization.addDefault("noPermission", "&e[SilkSpawnersEco] &4You do not have the permission to perform this operation!");
        localization.addDefault("commandUsage", "&e[SilkSpawnersEco] &4Command usage: /silkspawnerseco reload");
        localization.addDefault("reloadSuccess", "&e[SilkSpawnersEco] &2Config file successfully reloaded.");
        localization.options().copyDefaults(true);
        saveLocalization();
    }

    @SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", justification = "onEnable is our \"constructor\"")
    private void saveLocalization() {
        try {
            localization.save(localizationFile);
        } catch (final IOException e) {
            getLogger().log(Level.WARNING, "Failed to save the localization! Please report this! (I/O)", e);
        }
    }

    private void registerTask() {
        if (isConfirmation()) {
            getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    getPendingConfirmationList().clear();
                }
            }, getConfig().getInt("confirmation.delay") * TICKS_PER_SECOND, getConfig().getInt("confirmation.delay") * TICKS_PER_SECOND);
        }
    }

    @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", justification = "False positive")
    @SuppressWarnings("checkstyle:ReturnCount")
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault seems to be missing. Make sure to install the latest version of Vault!");
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null || rsp.getProvider() == null) {
            getLogger().severe("There is no economy provider installed for Vault! Make sure to install an economy plugin!");
            return false;
        }
        setEcon(rsp.getProvider());
        return getEcon() != null;
    }
}
