package de.dustplanet.silkspawnersecoaddon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.silkspawnersecoaddon.SilkSpawnersEcoAddon;
import de.dustplanet.silkspawnersecoaddon.util.SilkSpawnersEcoAddonUtil;

public class SilkSpawnersEcoAddonSpawnerBreakListener implements Listener {
    private SilkSpawnersEcoAddon plugin;
    private SilkSpawnersEcoAddonUtil util;

    public SilkSpawnersEcoAddonSpawnerBreakListener(SilkSpawnersEcoAddon instance) {
        plugin = instance;
        util = new SilkSpawnersEcoAddonUtil(plugin, plugin.getSilkUtil());
    }

    @EventHandler
    public void onSpawnerBreak(SilkSpawnersSpawnerBreakEvent event) {
        event.setCancelled(util.handleGenericEvent(event));
    }
}
