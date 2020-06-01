package de.dustplanet.silkspawnersecoaddon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerChangeEvent;
import de.dustplanet.silkspawnersecoaddon.SilkSpawnersEcoAddon;
import de.dustplanet.silkspawnersecoaddon.util.SilkSpawnersEcoAddonUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Block listener that calculates charging if a spawner is changed.
 *
 * @author timbru31
 */
public class SilkSpawnersEcoAddonSpawnerChangeListener implements Listener {
    private final SilkSpawnersEcoAddonUtil util;

    @SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public SilkSpawnersEcoAddonSpawnerChangeListener(final SilkSpawnersEcoAddon instance) {
        util = new SilkSpawnersEcoAddonUtil(instance, instance.getSilkUtil());
    }

    @EventHandler
    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void onSpawnerChange(final SilkSpawnersSpawnerChangeEvent event) {
        event.setCancelled(util.handleGenericEvent(event));
    }

}
