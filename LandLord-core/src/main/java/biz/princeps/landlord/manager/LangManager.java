package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.landlord.util.ConfigUtil;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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

    private ILandLord pl;
    private String filename;
    private FileConfiguration msg;

    public LangManager(ILandLord pl, String lang) {
        this.pl = pl;
        filename = "messages/" + lang + ".yml";
        reload();
        new ConfigUtil(pl).handleConfigUpdate(pl.getPlugin().getDataFolder() + "/" + filename, "/" + filename);
        reload();
    }

    @Override
    public void reload() {
        File f = new File(pl.getPlugin().getDataFolder(), filename);
        this.msg = new YamlConfiguration();
        try {
            File folder = new File(pl.getPlugin().getDataFolder(), "messages");
            if (!folder.exists())
                folder.mkdir();

            if (!f.exists())
                pl.getPlugin().saveResource(filename, false);

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

        if (message == null) {
            pl.getLogger().warning("Your language file " + filename + " seems to miss string '" + path + "'");
            return Lists.newArrayList();
        }

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
    public void sendMessage(CommandSender player, String msg) {
        if (msg.isEmpty() || msg.equals("null")) return;
        if (msg.equals("MISSING STRING") && pl.getConfig().getBoolean("CommandSettings.Main.enableMissingStringWarning")) {
            player.sendMessage("§cThe string you are looking for does not exist. Please check the log for further information!");
        } else {
            //I don't know why, but some messages are not sent correctly without TextComponent...
            player.spigot().sendMessage(TextComponent.fromLegacyText(msg));
        }
    }
}
