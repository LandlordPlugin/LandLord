package biz.princeps.lib;

import de.eldoria.eldoutilities.core.EldoUtilities;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class ConfigUpdater {

    /**
     * Source is the *.yml within the jar.
     * Destination is the yml copied into some directory.
     * <p>
     * The destination file is supposed to receive all entries, that are in source but not in dest.
     *
     * @param source the *.yml path
     * @param dest   the destination file
     */
    public static void updateConfig(String source, File dest) {
        File sourceFile = null;
        //sourceFile = new File(PrincepsLib.getPluginInstance().getClass().getResource(source));
        List<Entry> entryList = generateEntries(sourceFile);


    }


    private static List<Entry> generateEntries(File f) {
        List<Entry> entryList = new ArrayList<>();
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(f);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        Set<String> keys = config.getKeys(true);

        EldoUtilities.logger().log(Level.INFO, Arrays.toString(keys.toArray()));

        return entryList;
    }

    static class Entry {
        final Entry superior;

        final String entryname;
        final Object entry;
        final List<String> comment;

        public Entry(Entry superior, String entryname, Object entry, List<String> comment) {
            this.superior = superior;
            this.entryname = entryname;
            this.entry = entry;
            this.comment = comment;
        }
    }

}
