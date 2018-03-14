package biz.princeps.landlord.listener;

import biz.princeps.landlord.Landlord;
import org.bukkit.event.Listener;

/**
 * Created by spatium on 17.07.17.
 */
public abstract class BasicListener implements Listener {

    protected Landlord plugin;

    public BasicListener() {
        this.plugin = Landlord.getInstance();
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
