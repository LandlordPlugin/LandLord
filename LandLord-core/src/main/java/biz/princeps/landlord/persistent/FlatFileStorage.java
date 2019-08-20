package biz.princeps.landlord.persistent;

import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IStorage;
import biz.princeps.lib.util.SpigotUtil;
import biz.princeps.lib.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

public class FlatFileStorage implements IStorage {

    private JavaPlugin pl;

    private File customConfigFile;
    private FileConfiguration customConfig;

    public FlatFileStorage(JavaPlugin pl) {
        this.pl = pl;
    }

    public void init() {
        customConfigFile = new File(pl.getDataFolder(), "storage.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            try {
                customConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void getPlayer(UUID id, Consumer<IPlayer> consumer) {
        consumer.accept(getPlayer(id));
    }

    @Override
    public IPlayer getPlayer(UUID id) {
        ConfigurationSection sec = customConfig.getConfigurationSection(id.toString());

        if (sec == null) {
            return null;
        }

        return new LPlayer(id, Bukkit.getOfflinePlayer(id).getName(),
                sec.getInt("claims"),
                SpigotUtil.exactlocationFromString(sec.getString("home")),
                TimeUtil.stringToTime(sec.getString("lastlogin")));
    }

    @Override
    public void savePlayer(IPlayer p, boolean async) {
        ConfigurationSection sec = customConfig.createSection(p.getUuid().toString());
        sec.set("claims", p.getClaims());
        sec.set("home", SpigotUtil.exactlocationToString(p.getHome()));
        sec.set("lastlogin", TimeUtil.timeToString(p.getLastSeen()));

        if (async) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(pl, this::save);
        }
    }

    public synchronized void save() {
        try {
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
