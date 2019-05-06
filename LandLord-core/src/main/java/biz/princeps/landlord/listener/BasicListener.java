package biz.princeps.landlord.listener;

import biz.princeps.landlord.Landlord;
import org.bukkit.event.Listener;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/07/17
 *
 * All of landlords listeners extend this class. Registers the listener with spigot. Just need to construct it once.
 */
public abstract class BasicListener implements Listener {

    protected Landlord plugin;

    public BasicListener() {
        this.plugin = Landlord.getInstance();
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin.getPluginInstance());
    }
}
