package biz.princeps.landlord;

import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.storage.DatabaseAPI;
import co.aikar.commands.BukkitCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by spatium on 16.07.17.
 */
public class Landlord extends JavaPlugin {

    private static Landlord instance;
    private static DatabaseAPI databaseAPI;

    @Override
    public void onEnable(){
        manageCommands();

        PrincepsLib.setPluginInstance(this);

    }


    @Override
    public void onDisable(){

    }


    private void manageCommands() {
        BukkitCommandManager cmdmanager = new BukkitCommandManager(this);
        cmdmanager.registerCommand(new Landlordbase());
    }


    public static Landlord getInstance() {
        return instance;
    }
}
