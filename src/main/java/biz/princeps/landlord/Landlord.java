package biz.princeps.landlord;

import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.landlord.handler.WorldGuardHandler;
import biz.princeps.landlord.persistent.Requests;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.storage.DatabaseAPI;
import biz.princeps.lib.storage.DatabaseType;
import co.aikar.commands.BukkitCommandManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by spatium on 16.07.17.
 */
public class Landlord extends JavaPlugin {

    private static Landlord instance;
    private static DatabaseAPI databaseAPI;

    private WorldGuardHandler wgHandler;

    @Override
    public void onEnable() {
        // Dependency stuff
        if (getWorldGuard() == null) {
            getLogger().warning("WorldGuard not found! Please ensure you have the correct version of WorldGuard in order to use LandLord");
            getPluginLoader().disablePlugin(this);
            return;
        } else
            wgHandler = new WorldGuardHandler(getWorldGuard());

        instance = this;
        manageCommands();

        PrincepsLib.setPluginInstance(this);
        databaseAPI = new DatabaseAPI(DatabaseType.valueOf(getConfig().getString("DatabaseType")), getConfig(), new Requests(), "biz.princeps.landlord.persistent");
    }


    @Override
    public void onDisable() {

    }


    private void manageCommands() {
        BukkitCommandManager cmdmanager = new BukkitCommandManager(this);
        cmdmanager.registerCommand(new Landlordbase());
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }

        return (WorldGuardPlugin) plugin;
    }

    public static Landlord getInstance() {
        return instance;
    }

    public static DatabaseAPI getDatabaseAPI() {
        return databaseAPI;
    }

    public WorldGuardHandler getWgHandler() {
        return wgHandler;
    }
}
