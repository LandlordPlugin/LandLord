package com.jcdesimp.landlord.landManagement;

import com.jcdesimp.landlord.Landlord;
import org.bukkit.ChatColor;

import java.util.HashMap;

/**
 * File created by jcdesimp on 4/11/14.
 */
public class FlagManager {
    HashMap<String, Landflag> registeredFlags;
    Landlord plugin;

    public FlagManager(Landlord plugin) {
        this.registeredFlags = new HashMap<>();
        this.plugin = plugin;

    }

    public HashMap<String, Landflag> getRegisteredFlags() {
        return registeredFlags;
    }

    public boolean registerFlag(Landflag f) {
        if (registeredFlags.containsKey(f.getClass().getSimpleName())) {
            plugin.getLogger().warning("Could not register flag \"" + f.getClass().getSimpleName() + "\" because a flag is already registered with that name!");
            f.setUniqueName(f.getClass().getSimpleName());

            return false;
        }
        try {
            plugin.getServer().getPluginManager().registerEvents(f, plugin);
            registeredFlags.put(f.getClass().getSimpleName(), f);
        } catch (Exception e) {
            plugin.getLogger().warning("Error occured while registering flag \"" + f.getClass().getSimpleName() + "\":");
            e.printStackTrace();
            return false;
        }

        plugin.getLogger().info("Registered flag: " + f.getClass().getSimpleName());
        return true;
    }


    public Landflag getFlag(String desc) {
        for (Landflag flag : registeredFlags.values()) {
            if (flag.getAllowedTitle().equals(ChatColor.stripColor(desc)) || flag.getDeniedTitle().equals(ChatColor.stripColor(desc))) {
                return flag;
            }
        }
        return null;
    }

}

