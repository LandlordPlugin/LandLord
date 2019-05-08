package biz.princeps.landlord.manager;

import biz.princeps.landlord.ALandLord;
import biz.princeps.landlord.api.ILangManager;
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
public class LangManager implements ILangManager {

    private ALandLord pl;
    private String filename;
    private FileConfiguration msg;

    public LangManager(ALandLord pl, String lang) {
        this.pl = pl;
        filename = "messages/" + lang + ".yml";
        reload();
        ConfigUtil.handleConfigUpdate(pl.getDataFolder() + "/" + filename, "/" + filename);
        reload();
    }

    @Override
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

    @Override
    public String getString(String path) {
        String message = msg.getString(path);
        if (message == null) {
            pl.getLogger().warning("Your language file " + filename + " seems to miss string '" + path + "'");
            return "MISSING STRING";
        } else {
            return ChatColor.translateAlternateColorCodes('&', getTag() + " " + message);
        }
    }


    @Override
    public String getTag() {
        return ChatColor.translateAlternateColorCodes('&', msg.getString("Tag"));
    }

    @Override
    public List<String> getStringList(String path) {
        List<String> message = msg.getStringList(path);
        List<String> finishedFormatting = new ArrayList<>();
        for (String s : message) {
            finishedFormatting.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return finishedFormatting;
    }

    @Override
    public String getRawString(String path) {
        String message = msg.getString(path);
        if (message == null) {
            pl.getLogger().warning("Your language file " + filename + " seems to miss string '" + path + "'");
            return "MISSING STRING";
        } else {
            return ChatColor.translateAlternateColorCodes('&', message);
        }
    }

    @Override
    public void sendMessage(Player player, String msg) {
        if (msg.equals("MISSING STRING") &&
                pl.getConfig().getBoolean("CommandSettings.Main.enableMissingStringWarning")) {
            player.sendMessage("&cThe string you are looking for does not exist. Please check the log for further information!");
        } else if (msg.equals("null") || msg.isEmpty()) {
        } else player.sendMessage(msg);
    }
}
