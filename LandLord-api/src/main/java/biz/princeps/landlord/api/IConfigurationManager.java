package biz.princeps.landlord.api;

import org.bukkit.World;

/**
 * <p>This API interface <strong>must not be extended, implemented or overridden</strong>.</p>
 */
public interface IConfigurationManager {

    /**
     * Handle config file upgrade between different versions.
     * Stores the old one and creates the new one.
     *
     * @param pathToExisting the path to the real config file
     * @param pathInJar      the path to the up-to-date config in the jar archive
     */
    void handleConfigUpdate(String pathToExisting, String pathInJar);

    /**
     * Return a String value from a per-world configurable field.
     *
     * @param world        the world used to get the value
     * @param defaultPath  the path to access the default config option
     * @param defaultValue the default value in case it does not exist in the config
     * @return the requested String
     */
    String getCustomizableString(World world, String defaultPath, String defaultValue);

    /**
     * Return an int value from a per-world configurable field.
     *
     * @param world        the world used to get the value
     * @param defaultPath  the path to access the default config option
     * @param defaultValue the default value in case it does not exist in the config
     * @return the requested int
     */
    int getCustomizableInt(World world, String defaultPath, int defaultValue);

    /**
     * Return a boolean value from a per-world configurable field.
     *
     * @param world        the world used to get the value
     * @param defaultPath  the path to access the default config option
     * @param defaultValue the default value in case it does not exist in the config
     * @return the requested boolean
     */
    boolean getCustomizableBoolean(World world, String defaultPath, boolean defaultValue);

}
