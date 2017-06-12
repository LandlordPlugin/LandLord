package com.jcdesimp.landlord.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;

/**
 * File created by jcdesimp on 4/8/14.
 */
public class CustomConfig {

    private JavaPlugin plugin;

    private String resourceLocation;
    private String destination;

    private FileConfiguration customConfig = null;
    private File customConfigFile = null;

    public CustomConfig(JavaPlugin plugin, String resourceLocation, String destination) {
        this.plugin = plugin;
        this.resourceLocation = resourceLocation;
        this.destination = destination;

        Map<String, Object> oldConfig = get().getValues(true);

        //this.reload();
        //this.save();
        this.saveDefault();

        // checks for missing entries and applies new ones
        for (Map.Entry<String, Object> entry : customConfig.getDefaults().getValues(true).entrySet()) {
            if (oldConfig.containsKey(entry.getKey())) {
                customConfig.set(entry.getKey(), oldConfig.get(entry.getKey()));
            } else {
                customConfig.set(entry.getKey(), entry.getValue());
            }

        }

        save();
    }

    public void reload() {
        if (customConfigFile == null) {
            customConfigFile = new File(plugin.getDataFolder(), destination);
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            InputStream resource = plugin.getResource(resourceLocation);
            if (resource == null) {
                plugin.getLogger().severe("Internal resource missing at " + resourceLocation + "!");
                return;
            }
            defConfigStream = new InputStreamReader(resource, "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Error reading internal resource at " + resourceLocation + "!");
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            //todo
            /*try {
                defConfig.save(new File(plugin.getDataFolder(), destination));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            customConfig.setDefaults(defConfig);
        }
    }


    public FileConfiguration get() {
        if (customConfig == null) {
            reload();
        }

        return customConfig;
    }


    public void save() {
        if (customConfig == null || customConfigFile == null) {
            return;
        }
        try {
            get().save(customConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
        }
    }

    public void saveDefault() {
        if (customConfigFile == null) {
            customConfigFile = new File(plugin.getDataFolder(), destination);
        }
        if (!customConfigFile.exists()) {
            plugin.saveResource(resourceLocation, false);
        }
    }
}
