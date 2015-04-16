package de.dustplanet.silkspawnersecoaddon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

//Vault
import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
// Metrics
import org.mcstats.Metrics;

/**
 * General stuff (config).
 *
 * @author xGhOsTkiLLeRx
 */

public class SilkSpawnersEcoAddon extends JavaPlugin {
    /**
     * Config of SilkSpawnersEcoAddon.
     */
    private FileConfiguration config;
    /**
     * Real file of the config.
     */
    private File configFile;
    /**
     * Economy provider with Vault.
     */
    private Economy econ;
    /**
     * Default price for charging.
     */
    private double defaultPrice = 10.5;
    /**
     * Status if XP charging is on or off.
     */
    private boolean chargeXP;
    /**
     * Status of the confirmation feature.
     */
    private boolean confirmation;
    /**
     * List of pending player who need to confirm the change.
     */
    private ArrayList<UUID> pendingConfirmationList = new ArrayList<>();

    /**
     * Disabled SilkSpawnersEcoAddon and cleans stuff.
     */
    @Override
    public void onDisable() {
        disable();
    }

    /**
     * Loads SilkSpawnersEcodAddon.
     */
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

        // CommandExecutor
        getCommand("silkspawnerseco").setExecutor(new SilkSpawnersEcoAddonCommandExecutor(this));

        if (setupEconomy()) {
            // If Vault is enabled, load the economy
            getLogger().info("Loaded Vault successfully");
        } else {
            // Else tell the admin about the missing of Vault
            getLogger().severe("Vault was not found! XP charging is now ON...");
            setChargeXP(true);
        }

        // Listeners
        getServer().getPluginManager().registerEvents(new SilkSpawnersEcoAddonListener(this), this);

        // Metrics
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            getLogger().info("Couldn't start Metrics, please report this!");
            e.printStackTrace();
        }

        // Register task
        registerTask();
    }

    /**
     * Loads the default config and values.
     */
    private void loadConfig() {
        config.options().header("You can configure every entityID/name (without spaces) or a default!");
        config.addDefault("cantAfford", "&e[SilkSpawnersEco] &4Sorry, but you can't change the mob of this spawner, because you have not enough money!");
        config.addDefault("afford", "&e[SilkSpawnersEco] &2This action costs &e%money%");
        config.addDefault("sameMob", "&e[SilkSpawnersEco] &2This action was free, because it's the same mob!");
        config.addDefault("confirmationPending", "&e[SilkSpawnersEco] Remember that changing the spawner costs &2%money%&e, if you want to continue, do the action again!");
        config.addDefault("noPermission", "&e[SilkSpawnersEco] &2You do not have the permission to perfom this operation!");
        config.addDefault("commandUsage", "&e[SilkSpawnersEco] &2Command usage: /silkspawnerseco reload");
        config.addDefault("reloadSuccess", "&e[SilkSpawnersEco] &4SilkSpawnersEcoAddon config file successfully reloaded.");
        config.addDefault("chargeSameMob", false);
        config.addDefault("chargeXP", false);
        config.addDefault("chargeMultipleAmounts", false);
        config.addDefault("confirmation.enabled", false);
        config.addDefault("confirmation.delay", 30);
        config.addDefault("default", 10.5);
        config.addDefault("pig", 7.25);
        config.addDefault("cow", 0.00);
        config.options().copyDefaults(true);
        saveConfig();
        setDefaultPrice(config.getDouble("default"));
        setChargeXP(config.getBoolean("chargeXP"));
        setConfirmation(config.getBoolean("confirmation.enabled"));
    }

    /**
     * Reloads the configuration from the file
     */
    public void reload() {
        disable();
        this.reloadConfig();
        setDefaultPrice(config.getDouble("default"));
        setChargeXP(config.getBoolean("chargeXP"));
        setConfirmation(config.getBoolean("confirmation.enabled"));
        registerTask();
    }

    /**
     * Clears all important data
     */
    private void disable() {
        getServer().getScheduler().cancelTasks(this);
        getPendingConfirmationList().clear();
    }

    // Initialized to work with Vault
    /**
     * Hook into Vault.
     *
     * @return whether the hook into Vault was successful
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        setEcon(rsp.getProvider());
        return getEcon() != null;
    }

    // If no config is found, copy the default one(s)!
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

    /**
     * Registers task for confirmation list
     */
    private void registerTask() {
        // Task if needed
        if (confirmation()) {
            getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    // Clear pending list
                    getPendingConfirmationList().clear();
                }
            }, getConfig().getInt("confirmation.delay") * 20L, getConfig().getInt("confirmation.delay") * 20L);
        }
    }

    /**
     * Gets the default price used for charging.
     *
     * @return the default price
     */
    public double getDefaultPrice() {
        return defaultPrice;
    }

    /**
     * Sets the default price used for charging.
     *
     * @param defaultPrice the default price
     */
    public void setDefaultPrice(double defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    /**
     * Gets current state if XP charging is on.
     *
     * @return the result
     */
    public boolean chargeXP() {
        return chargeXP;
    }

    /**
     * Sets XP charging on or off.
     *
     * @param chargeXP the new state
     */
    public void setChargeXP(boolean chargeXP) {
        this.chargeXP = chargeXP;
    }

    /**
     * Returns Vault economy instance.
     *
     * @return economy or if not found null
     */
    public Economy getEcon() {
        return econ;
    }

    /**
     * Sets the Vault economy.
     *
     * @param econ the Vault econ
     */
    public void setEcon(Economy econ) {
        this.econ = econ;
    }

    /**
     * Gets the list of people who need to confirm the change.
     *
     * @return the list of players
     */
    public ArrayList<UUID> getPendingConfirmationList() {
        return pendingConfirmationList;
    }

    /**
     * Sets the pending list of players.
     *
     * @param pendingConfirmationList the new player pending list
     */
    public void setPendingConfirmationList(ArrayList<UUID> pendingConfirmationList) {
        this.pendingConfirmationList = pendingConfirmationList;
    }

    /**
     * Returns if the confirmation feature is used.
     *
     * @return the result
     */
    public boolean confirmation() {
        return confirmation;
    }

    /**
     * Sets if configuration feature should be used.
     *
     * @param confirmation true or false
     */
    public void setConfirmation(boolean confirmation) {
        this.confirmation = confirmation;
    }
}
