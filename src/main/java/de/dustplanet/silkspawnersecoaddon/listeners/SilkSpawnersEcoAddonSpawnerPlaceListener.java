package de.dustplanet.silkspawnersecoaddon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerPlaceEvent;
import de.dustplanet.silkspawnersecoaddon.SilkSpawnersEcoAddon;
import de.dustplanet.silkspawnersecoaddon.util.SilkSpawnersEcoAddonUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class SilkSpawnersEcoAddonSpawnerPlaceListener implements Listener {
    private SilkSpawnersEcoAddonUtil util;

    @SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
    public SilkSpawnersEcoAddonSpawnerPlaceListener(SilkSpawnersEcoAddon instance) {
        util = new SilkSpawnersEcoAddonUtil(instance, instance.getSilkUtil());
    }

    @EventHandler
    public void onSpawnerPlace(SilkSpawnersSpawnerPlaceEvent event) {
        event.setCancelled(util.handleGenericEvent(event));
    }
}
