package biz.princeps.landlord;

import biz.princeps.landlord.commands.Landlordbase;
import co.aikar.commands.BukkitCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by spatium on 16.07.17.
 */
public class Landlord extends JavaPlugin {

    private static Landlord instance;

    @Override
    public void onEnable(){
        manageCommands();

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
