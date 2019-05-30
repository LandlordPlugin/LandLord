package biz.princeps.landlord.api;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 4/6/18
 * <p>
 * Options class, which pulls some stuff out of the config.
 */
public class Options {

    private static FileConfiguration cfg;
    private static boolean hasVault;
    private static int MANAGE_SIZE;
    private static Set<String> toggleMobs;

    public static boolean enabled_inactiveBuyUp() {
        return cfg.getBoolean("BuyUpInactive.enable");
    }

    public static boolean isVaultEnabled() {
        return hasVault;
    }

    public static boolean enabled_borders() {
        return cfg.getBoolean("Borders.enable");
    }

    public static boolean enabled_map() {
        return cfg.getBoolean("Map.enable");
    }

    public static boolean enabled_shop() {
        return cfg.getBoolean("Shop.enable");
    }

    public static boolean enabled_homes() {
        return cfg.getBoolean("Homes.enable");
    }

    public static int getManageSize() {
        if (MANAGE_SIZE < 1) {
            ConfigurationSection section = cfg.getConfigurationSection("Manage");

            Set<String> keys = section.getKeys(true);

            int trues = 0;
            for (
                    String key : keys) {
                if (section.getBoolean(key))
                    trues++;
            }
            MANAGE_SIZE = (trues / 9 + (trues % 9 == 0 ? 0 : 1)) * 9;
        }
        return MANAGE_SIZE;
    }

    public static Set<String> getToggleMobs() {
        if (toggleMobs == null) {
            toggleMobs = new HashSet<>(cfg.getStringList("Manage.mob-spawning.toggleableMobs"));
        }
        return toggleMobs;
    }

    public static void setConfig(FileConfiguration config, boolean hasVault) {
        cfg = config;
        Options.hasVault = cfg.getBoolean("Economy.enable") && hasVault;
    }
}
