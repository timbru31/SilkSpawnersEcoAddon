package de.dustplanet.silkspawnersecoaddon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerChangeEvent;
import de.dustplanet.silkspawnersecoaddon.SilkSpawnersEcoAddon;
import de.dustplanet.silkspawnersecoaddon.util.SilkSpawnersEcoAddonUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This is the listener of the custom event to charge the user.
 *
 * @author xGhOsTkiLLeRx
 */

public class SilkSpawnersEcoAddonSpawnerChangeListener implements Listener {
    private SilkSpawnersEcoAddonUtil util;

    @SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
    public SilkSpawnersEcoAddonSpawnerChangeListener(SilkSpawnersEcoAddon instance) {
        util = new SilkSpawnersEcoAddonUtil(instance, instance.getSilkUtil());
    }

    @EventHandler
    public void onSpawnerChange(SilkSpawnersSpawnerChangeEvent event) {
        event.setCancelled(util.handleGenericEvent(event));
    }

}
