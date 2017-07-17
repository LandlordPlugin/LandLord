package biz.princeps.landlord.manager;

import biz.princeps.landlord.Landlord;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by spatium on 17.07.17.
 */
public class LangManager {

    private FileConfiguration msg;

    public LangManager(Landlord pl, String lang) {
        String filename = "messages/" + lang + ".yml";
        File f = new File(pl.getDataFolder(), filename);
        this.msg = new YamlConfiguration();
        try {
            File folder = new File(pl.getDataFolder(), "messages");
            if (!folder.exists())
                folder.mkdir();

            pl.saveResource(filename, false);

            this.msg.load(f);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getString(String path){
        String message = msg.getString(path);
        return ChatColor.translateAlternateColorCodes('&', getTag() + " " + message);
    }


    public String getTag(){
        return ChatColor.translateAlternateColorCodes('&', msg.getString("Tag"));
    }
}
