package de.dustplanet.silkspawnersecoaddon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerChangeEvent;
import de.dustplanet.silkspawnersecoaddon.SilkSpawnersEcoAddon;
import de.dustplanet.silkspawnersecoaddon.util.SilkSpawnersEcoAddonUtil;

/**
 * This is the listener of the custom event to charge the user.
 *
 * @author xGhOsTkiLLeRx
 */

public class SilkSpawnersEcoAddonSpawnerChangeListener implements Listener {
    private SilkSpawnersEcoAddon plugin;
    private SilkSpawnersEcoAddonUtil util;

    public SilkSpawnersEcoAddonSpawnerChangeListener(SilkSpawnersEcoAddon instance) {
        plugin = instance;
        util = new SilkSpawnersEcoAddonUtil(plugin, plugin.getSilkUtil());
    }

    @EventHandler
    public void onSpawnerChange(SilkSpawnersSpawnerChangeEvent event) {
        event.setCancelled(util.handleGenericEvent(event));
    }

}
