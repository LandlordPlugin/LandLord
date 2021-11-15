package biz.princeps.lib;

import biz.princeps.lib.command.CommandManager;
import biz.princeps.lib.crossversion.CrossVersion;
import biz.princeps.lib.crossversion.Stuff;
import biz.princeps.lib.item.ItemManager;
import biz.princeps.lib.manager.ConfirmationManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by spatium on 18.06.17.
 */
public class PrincepsLib extends JavaPlugin implements Listener {

    private static JavaPlugin INSTANCE;
    private static CrossVersion CROSS_VERSION;
    private static ItemManager ITEM_MANAGER;
    private static CommandManager COMMAND_MANAGER;
    private static ConfirmationManager CONFIRMATION_MANAGER;
    private static Stuff STUFF_MANAGER;
    private static TranslateableStrings TRANSLATEABLE_STRINGS;

    /**
     * @return your own plugin instance, which you set before
     */
    public static JavaPlugin getPluginInstance() {
        return INSTANCE;
    }

    /**
     * You need to call this method in order to assign your own plugin instance to this api.
     *
     * @param plugin the plugin instance
     */
    public static void setPluginInstance(JavaPlugin plugin) {
        INSTANCE = plugin;
        CROSS_VERSION = new CrossVersion();
        ITEM_MANAGER = new ItemManager();
        COMMAND_MANAGER = new CommandManager();
        CONFIRMATION_MANAGER = new ConfirmationManager(plugin);
        STUFF_MANAGER = new Stuff(plugin);
        TRANSLATEABLE_STRINGS = new TranslateableStrings();
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
                INSTANCE.getDataFolder().mkdirs();
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
        return CROSS_VERSION;
    }

    public static ItemManager getItemManager() {
        return ITEM_MANAGER;
    }

    public static CommandManager getCommandManager() {
        return COMMAND_MANAGER;
    }

    public static ConfirmationManager getConfirmationManager() {
        return CONFIRMATION_MANAGER;
    }

    public static Stuff getStuffManager() {
        return STUFF_MANAGER;
    }

    public static TranslateableStrings getTranslateableStrings() {
        return TRANSLATEABLE_STRINGS;
    }

    @Override
    public void onEnable() {
        setPluginInstance(this);
    }

}
