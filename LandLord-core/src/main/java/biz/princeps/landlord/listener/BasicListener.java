package biz.princeps.landlord.listener;

import biz.princeps.landlord.api.ILandLord;
import org.bukkit.event.Listener;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/07/17
 * <p>
 * All of landlords listeners extend this class. Registers the listener with spigot. Just need to construct it once.
 */
public abstract class BasicListener implements Listener {

    protected final ILandLord plugin;

    public BasicListener(ILandLord plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin.getPlugin());
    }

}
