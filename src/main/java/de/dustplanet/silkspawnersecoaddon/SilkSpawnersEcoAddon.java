package de.dustplanet.silkspawnersecoaddon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.dustplanet.silkspawnersecoaddon.commands.SilkSpawnersEcoAddonCommandExecutor;
import de.dustplanet.silkspawnersecoaddon.listeners.SilkSpawnersEcoAddonSpawnerBreakListener;
import de.dustplanet.silkspawnersecoaddon.listeners.SilkSpawnersEcoAddonSpawnerChangeListener;
import de.dustplanet.silkspawnersecoaddon.listeners.SilkSpawnersEcoAddonSpawnerPlaceListener;
import de.dustplanet.silkspawnersecoaddon.util.ScalarYamlConfiguration;
import de.dustplanet.util.SilkUtil;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;

/**
 * General stuff (config).
 *
 * @author xGhOsTkiLLeRx
 */

public class SilkSpawnersEcoAddon extends JavaPlugin {
    private static final int BSTATS_PLUGIN_ID = 550;
    private static final long TICKS_PER_SECOND = 20L;
    @Getter
    @Setter
    private FileConfiguration localization;
    private File configFile, localizationFile;
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
    private ArrayList<UUID> pendingConfirmationList = new ArrayList<>();
    @Getter
    @Setter
    private SilkUtil silkUtil;

    @Override
    public void onDisable() {
        disable();
    }

    @SuppressWarnings("unused")
    @Override
    public void onEnable() {
        setSilkUtil(SilkUtil.hookIntoSilkSpanwers());

        configFile = new File(getDataFolder(), "config.yml");
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

        localizationFile = new File(getDataFolder(), "localization.yml");
        if (!localizationFile.exists()) {
            copy("localization.yml", localizationFile);
        }

        setLocalization(ScalarYamlConfiguration.loadConfiguration(localizationFile));
        loadLocalization();

        getCommand("silkspawnerseco").setExecutor(new SilkSpawnersEcoAddonCommandExecutor(this));

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

    public void reload() {
        disable();
        this.reloadConfig();
        FileConfiguration config = getConfig();
        setConfirmation(config.getBoolean("confirmation.enabled"));
        registerTask();
    }

    private void copy(String yml, File file) {
        try (OutputStream out = new FileOutputStream(file); InputStream in = getResource(yml)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            getLogger().warning("Failed to copy the default config! (I/O)");
            e.printStackTrace();
        }
    }

    private void disable() {
        getServer().getScheduler().cancelTasks(this);
        getPendingConfirmationList().clear();
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        config.options().header("You can configure every mob name (without spaces) or a default!");
        config.addDefault("chargeSameMob", false);
        config.addDefault("chargeXP", false);
        config.addDefault("chargeBoth", false);
        config.addDefault("chargeMultipleAmounts", false);
        config.addDefault("confirmation.enabled", false);
        config.addDefault("confirmation.delay", 30);
        config.addDefault("default.money.break", 10.5);
        config.addDefault("default.money.change", 10.5);
        config.addDefault("default.money.place", 10.5);
        config.addDefault("default.xp.break", 100);
        config.addDefault("default.xp.change", 100);
        config.addDefault("default.xp.place", 100);
        config.addDefault("pig.money.break", 7.25);
        config.addDefault("pig.money.change", 7.25);
        config.addDefault("pig.money.place", 7.25);
        config.addDefault("pig.xp.break", 200);
        config.addDefault("pig.xp.change", 200);
        config.addDefault("pig.xp.place", 200);
        config.addDefault("cow.money.break", 0.00);
        config.addDefault("cow.money.change", 0.00);
        config.addDefault("cow.money.place", 0.00);
        config.addDefault("cow.xp.break", 20);
        config.addDefault("cow.xp.change", 20);
        config.addDefault("cow.xp.place", 20);
        config.options().copyDefaults(true);
        saveConfig();

        setChargeXP(config.getBoolean("chargeXP"));
        setChargeBoth(config.getBoolean("chargeBoth"));
        setConfirmation(config.getBoolean("confirmation.enabled"));
    }

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

    private void saveLocalization() {
        try {
            localization.save(localizationFile);
        } catch (IOException e) {
            getLogger().warning("Failed to save the localization! Please report this! (I/O)");
            e.printStackTrace();
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

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault seems to be missing. Make sure to install the latest version of Vault!");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null || rsp.getProvider() == null) {
            getLogger().severe("There is no economy provider installed for Vault! Make sure to install an economy plugin!");
            return false;
        }
        setEcon(rsp.getProvider());
        return getEcon() != null;
    }
}
