package de.dustplanet.silkspawnersecoaddon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerPlaceEvent;
import de.dustplanet.silkspawnersecoaddon.SilkSpawnersEcoAddon;
import de.dustplanet.silkspawnersecoaddon.util.SilkSpawnersEcoAddonUtil;

public class SilkSpawnersEcoAddonSpawnerPlaceListener implements Listener {
    private SilkSpawnersEcoAddon plugin;
    private SilkSpawnersEcoAddonUtil util;

    public SilkSpawnersEcoAddonSpawnerPlaceListener(SilkSpawnersEcoAddon instance) {
        plugin = instance;
        util = new SilkSpawnersEcoAddonUtil(plugin, plugin.getSilkUtil());
    }

    @EventHandler
    public void onSpawnerPlace(SilkSpawnersSpawnerPlaceEvent event) {
        event.setCancelled(util.handleGenericEvent(event));
    }
}
