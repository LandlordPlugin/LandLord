package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.IConfigurationManager;
import biz.princeps.landlord.api.ILandLord;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigurationManager implements IConfigurationManager {

    private static final String WORLDS_SECTION = "worlds";

    private final ILandLord plugin;
    private final FileConfiguration configuration;

    public ConfigurationManager(ILandLord plugin) {
        this.plugin = plugin;
        this.configuration = plugin.getConfig();
    }

    /**
     * In case you upgraded the config version (adding a field...) you have to increment the variable "version" in the
     * config.yml. This will cause the old config to be backuped and the new (changed) config to be copied in the
     * right place.
     */
    public void handleConfigUpdate(String pathToExisting, String pathInJar) {
        if (pathInJar == null || pathToExisting == null)
            return;

        FileConfiguration config = new YamlConfiguration();
        File existing = new File(pathToExisting);
        try {
            config.load(existing);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        int version = config.getInt("version");

        InputStream resourceAsStream = plugin.getClass().getResourceAsStream(pathInJar);
        BufferedReader reader;
        if (resourceAsStream != null)
            reader = new BufferedReader(new InputStreamReader(resourceAsStream));
        else {
            plugin.getLogger().warning("You are using an unknown translation.\n" +
                    "Please be aware, that LandLord will not add any new strings to your translation.\n" +
                    "If you would like to see your translation inside the plugin, please contact the author!");
            return;
        }
        reader.lines().forEach(s -> {
            if (s.startsWith("version:")) {
                try {
                    int i = Integer.parseInt(s.split(":")[1].trim());

                    if (i > version) {
                        existing.renameTo(new File(pathToExisting + ".v" + version));
                    }

                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid version in file " + pathInJar);
                }
            }
        });
        try {
            resourceAsStream.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCustomizableString(World world, String defaultPath, String defaultValue) {
        return configuration.getString(WORLDS_SECTION + "." + world.getName() + "." + defaultPath,
                configuration.getString(defaultPath, defaultValue));
    }

    @Override
    public int getCustomizableInt(World world, String defaultPath, int defaultValue) {
        return configuration.getInt(WORLDS_SECTION + "." + world.getName() + "." + defaultPath,
                configuration.getInt(defaultPath, defaultValue));
    }

    @Override
    public boolean getCustomizableBoolean(World world, String defaultPath, boolean defaultValue) {
        return configuration.getBoolean(WORLDS_SECTION + "." + world.getName() + "." + defaultPath,
                configuration.getBoolean(defaultPath, defaultValue));
    }
}
