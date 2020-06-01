package de.dustplanet.silkspawnersecoaddon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.silkspawnersecoaddon.SilkSpawnersEcoAddon;
import de.dustplanet.silkspawnersecoaddon.util.SilkSpawnersEcoAddonUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Block listener that calculates charging if a spawner is mined.
 *
 * @author timbru31
 */
public class SilkSpawnersEcoAddonSpawnerBreakListener implements Listener {
    private final SilkSpawnersEcoAddonUtil util;

    @SuppressFBWarnings({ "FCCD_FIND_CLASS_CIRCULAR_DEPENDENCY", "CD_CIRCULAR_DEPENDENCY", "IMC_IMMATURE_CLASS_NO_TOSTRING" })
    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public SilkSpawnersEcoAddonSpawnerBreakListener(final SilkSpawnersEcoAddon instance) {
        util = new SilkSpawnersEcoAddonUtil(instance, instance.getSilkUtil());
    }

    @EventHandler
    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void onSpawnerBreak(final SilkSpawnersSpawnerBreakEvent event) {
        event.setCancelled(util.handleGenericEvent(event));
    }
}
