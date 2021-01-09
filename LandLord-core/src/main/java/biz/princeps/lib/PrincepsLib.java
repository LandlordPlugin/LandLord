package biz.princeps.lib;

import biz.princeps.lib.command.CommandManager;
import biz.princeps.lib.crossversion.CrossVersion;
import biz.princeps.lib.crossversion.Stuff;
import biz.princeps.lib.item.ItemManager;
import biz.princeps.lib.manager.ConfirmationManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by spatium on 18.06.17.
 */
public class PrincepsLib extends JavaPlugin implements Listener {

    private static JavaPlugin instance;
    private static CrossVersion crossVersion;
    private static ItemManager itemManager;
    private static CommandManager commandManager;
    private static ConfirmationManager confirmationManager;
    private static Stuff stuffManager;
    private static TranslateableStrings translateableStrings;
    private static ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        setPluginInstance(this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
    }

    /**
     * @return your own plugin instance, which you set before
     */
    public static JavaPlugin getPluginInstance() {
        return instance;
    }


    /**
     * You need to call this method in order to assign your own plugin instance to this api
     *
     * @param instance
     */
    public static void setPluginInstance(JavaPlugin instance) {
        PrincepsLib.instance = instance;
        PrincepsLib.crossVersion = new CrossVersion();
        PrincepsLib.itemManager = new ItemManager();
        PrincepsLib.commandManager = new CommandManager();
        PrincepsLib.confirmationManager = new ConfirmationManager();
        PrincepsLib.stuffManager = new Stuff();
        PrincepsLib.translateableStrings = new TranslateableStrings();
        PrincepsLib.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    /**
     * Generates a myqsl-data file in your plugin folder
     *
     * @return the specific fileconfig
     */
    public static FileConfiguration prepareDatabaseFile() {
        File file = new File(getPluginInstance().getDataFolder(), "MySQL.yml");

        if (!file.exists())
            try {
                getPluginInstance().getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        config.addDefault("MySQL.Hostname", "localhost");
        config.addDefault("MySQL.Port", 3306);
        config.addDefault("MySQL.Database", "minecraft");
        config.addDefault("MySQL.User", "root");
        config.addDefault("MySQL.Password", "passy");
        config.options().copyDefaults(true);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return config;
    }


    public static CrossVersion crossVersion() {
        return crossVersion;
    }

    public static ItemManager getItemManager() {
        return itemManager;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static ConfirmationManager getConfirmationManager() {
        return confirmationManager;
    }

    public static Stuff getStuffManager() {
        return stuffManager;
    }

    public static TranslateableStrings getTranslateableStrings() {
        return translateableStrings;
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
