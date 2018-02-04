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
import de.dustplanet.silkspawnersecoaddon.listeners.SilkSpawnersEcoSpawnerChangeListener;
import lombok.Getter;
import lombok.Setter;

import net.milkbowl.vault.economy.Economy;

/**
 * General stuff (config).
 *
 * @author xGhOsTkiLLeRx
 */

public class SilkSpawnersEcoAddon extends JavaPlugin {

    private static final long TICKS_PER_SECOND = 20L;
    private FileConfiguration config;
    private File configFile;
    @Getter
    @Setter
    private Economy econ;
    @Getter
    @Setter
    private double defaultPrice = 10.5;
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

    /**
     * Copies default config file.
     *
     * @param yml the yml file string
     * @param file the actual file
     */
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
        config.options().header("You can configure every entityID/name (without spaces) or a default!");
        config.addDefault("cantAffordXP", "&e[SilkSpawnersEco] &4Sorry, but you can't change the mob of this spawner, because you have not enough EXP!");
        config.addDefault("cantAffordMoney", "&e[SilkSpawnersEco] &4Sorry, but you can't change the mob of this spawner, because you have not enough money!");
        config.addDefault("cantAffordBothXP", "&e[SilkSpawnersEco] &4Sorry, but you can't change the mob of this spawner, because you have not enough EXP! Nothing was charged.");
        config.addDefault("cantAffordBothMoney", "&e[SilkSpawnersEco] &4Sorry, but you can't change the mob of this spawner, because you have not enough money! Nothing was charged.");
        config.addDefault("afford", "&e[SilkSpawnersEco] &2This action costs &e%money%");
        config.addDefault("sameMob", "&e[SilkSpawnersEco] &2This action was free, because it's the same mob!");
        config.addDefault("confirmationPending", "&e[SilkSpawnersEco] Remember that changing the spawner costs &2%money%&e, if you want to continue, do the action again!");
        config.addDefault("noPermission", "&e[SilkSpawnersEco] &4You do not have the permission to perfom this operation!");
        config.addDefault("commandUsage", "&e[SilkSpawnersEco] &4Command usage: /silkspawnerseco reload");
        config.addDefault("reloadSuccess", "&e[SilkSpawnersEco] &2Config file successfully reloaded.");
        config.addDefault("chargeSameMob", false);
        config.addDefault("chargeXP", false);
        config.addDefault("chargeBoth", false);
        config.addDefault("chargeMultipleAmounts", false);
        config.addDefault("confirmation.enabled", false);
        config.addDefault("confirmation.delay", 30);
        config.addDefault("default.money", 10.5);
        config.addDefault("default.exp", 100);
        config.addDefault("pig.money", 7.25);
        config.addDefault("pig.exp", 200);
        config.addDefault("cow.money", 0.00);
        config.addDefault("cow.exp", 20);
        config.options().copyDefaults(true);
        saveConfig();
        setDefaultPrice(config.getDouble("default"));
        setChargeXP(config.getBoolean("chargeXP"));
        setChargeBoth(config.getBoolean("chargeBoth"));
        setConfirmation(config.getBoolean("confirmation.enabled"));
    }

    @Override
    public void onDisable() {
        disable();
    }

    @Override
    public void onEnable() {
        // Config
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

        config = getConfig();
        loadConfig();

        getCommand("silkspawnerseco").setExecutor(new SilkSpawnersEcoAddonCommandExecutor(this));

        if (setupEconomy()) {
            getLogger().info("Loaded Vault successfully");
        } else {
            getLogger().severe("Vault was not found! XP charging is now ON...");
            setChargeXP(true);
        }

        getServer().getPluginManager().registerEvents(new SilkSpawnersEcoSpawnerChangeListener(this), this);

        new Metrics(this);

        registerTask();
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

    public void reload() {
        disable();
        this.reloadConfig();
        config = getConfig();
        setDefaultPrice(config.getDouble("default"));
        setConfirmation(config.getBoolean("confirmation.enabled"));
        registerTask();
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
