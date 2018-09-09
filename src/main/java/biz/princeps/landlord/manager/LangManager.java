package biz.princeps.landlord.manager;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.util.ConfigUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class LangManager {

    private Landlord pl;
    private String filename;
    private FileConfiguration msg;

    public LangManager(Landlord pl, String lang) {
        this.pl = pl;
        filename = "messages/" + lang + ".yml";
        reload();
        ConfigUtil.handleConfigUpdate(pl.getDataFolder() + "/" + filename, "/" + filename);
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
        if (message == null) {
            pl.getLogger().warning("Your language file " + filename + " seems to miss string '" + path + "'");
            return "MISSING STRING";
        } else {
            return ChatColor.translateAlternateColorCodes('&', getTag() + " " + message);
        }
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
        if (message == null) {
            pl.getLogger().warning("Your language file " + filename + " seems to miss string '" + path + "'");
            return "MISSING STRING";
        } else {
            return ChatColor.translateAlternateColorCodes('&', message);
        }
    }

    public void sendMessage(Player player, String msg) {
        if (msg.equals("null") || msg.isEmpty()) {
        } else player.sendMessage(msg);
    }
}
