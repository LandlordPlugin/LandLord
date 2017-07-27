package biz.princeps.landlord.manager;

import biz.princeps.landlord.Landlord;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by spatium on 17.07.17.
 */
public class LangManager {

    private FileConfiguration msg;
    private Landlord pl;
    private String filename;

    public LangManager(Landlord pl, String lang) {
        this.pl = pl;
        filename = "messages/" + lang + ".yml";
        reload();
    }

    public void reload() {
        File f = new File(pl.getDataFolder(), filename);
        this.msg = new YamlConfiguration();
        try {
            File folder = new File(pl.getDataFolder(), "messages");
            if (!folder.exists())
                folder.mkdir();

            if (!f.exists())
                pl.saveResource(filename, false);

            this.msg.load(f);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getString(String path) {
        String message = msg.getString(path);
        return ChatColor.translateAlternateColorCodes('&', getTag() + " " + message);
    }


    public String getTag() {
        return ChatColor.translateAlternateColorCodes('&', msg.getString("Tag"));
    }

    public List<String> getStringList(String path) {
        List<String> message = msg.getStringList(path);
        List<String> finishedFormatting = new ArrayList<>();
        for (String s : message) {
            finishedFormatting.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return finishedFormatting;
    }

    public String getRawString(String path) {
        String message = msg.getString(path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
