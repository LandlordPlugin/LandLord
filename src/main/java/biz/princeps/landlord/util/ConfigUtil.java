package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class ConfigUtil {

    public static void handleConfigUpdate(String pathToExisting, String pathInJar) {

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

        InputStream resourceAsStream = Landlord.getInstance().getClass().getResourceAsStream(pathInJar);
        BufferedReader reader;
        if (resourceAsStream != null)
            reader = new BufferedReader(new InputStreamReader(resourceAsStream));
        else {
            Landlord.getInstance().getLogger().warning("You are using an unknown translation.\n" +
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
                    Landlord.getInstance().getLogger().warning("Invalid version in file " + pathInJar);
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
}
