package biz.princeps.landlord.persistent;

import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IStorage;
import biz.princeps.lib.storage.Datastorage;
import biz.princeps.lib.util.SpigotUtil;
import biz.princeps.lib.util.TimeUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Consumer;

public class SQLStorage extends Datastorage implements IStorage {

    private static final int CURRENT_VERSION = 4;

    public SQLStorage(JavaPlugin plugin) {
        super(plugin,
                plugin.getConfig().getString("MySQL.Hostname"),
                plugin.getConfig().getString("MySQL.Port"),
                plugin.getConfig().getString("MySQL.User"),
                plugin.getConfig().getString("MySQL.Password"),
                plugin.getConfig().getString("MySQL.Database")
        );
        executeQuery("SELECT version FROM ll_version", this::handleUpgrade);
    }

    private void handleUpgrade(ResultSet res) {
        try {
            res.next();
            int localVersion = res.getInt("version");

            boolean hasUpgraded = false;
            while (localVersion < CURRENT_VERSION) {
                executeUpgrade(localVersion);
                localVersion++;
                hasUpgraded = true;
            }

            if (hasUpgraded)
                executeAsync("UPDATE version FROM ll_version SET version = ?", CURRENT_VERSION);
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Error while handling upgrade!\nError:" + e.getMessage());
        }
    }

    /**
     * If you ever need to change the database scheme (adding a row...) you will need this method to alter the scheme
     * for everyone else aswell.
     * To upgrade the scheme follow these steps:
     * 1) backup your existing database
     * 2) increment the CURRENT_VERSION
     * 3) add a case with the previous version here
     * 4) Write some SQL statement that alters the table.
     */
    private void executeUpgrade(int localVersion) {
        switch (localVersion) {
            case 3:
                // TODO Handle upgrade from old scheme before this patch to v4
                // 19-05-02 whatever, screw this. Yea I never had the motivation to implement this migration. No one
                // complained, so here we are :shrug:
                break;
        }
    }

    @Override
    protected void setupDatabase() {
        execute("CREATE TABLE IF NOT EXISTS ll_players (" +
                "uuid VARCHAR(36)       NOT NULL," +
                "name VARCHAR(16)," +
                "claims INTEGER," +
                "home TEXT," +
                "lastseen VARCHAR(50)," +
                "PRIMARY KEY(uuid)" +
                ");");

        execute("CREATE TABLE IF NOT EXISTS ll_version(" +
                "version TINYINT," +
                "PRIMARY KEY(version)" +
                ");");

        execute("INSERT INTO ll_version (version) SELECT " + CURRENT_VERSION +
                " FROM DUAL WHERE NOT EXISTS (SELECT * FROM ll_version)");
    }


    @Override
    public void getPlayer(UUID id, Consumer<IPlayer> consumer) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> consumer.accept(getPlayer(id)));
    }

    @Override
    public IPlayer getPlayer(UUID id) {
        Triplet triplet = executeQuery("SELECT * FROM ll_players WHERE uuid = '" + id + "'");
        // plugin.getLogger().log(Level.INFO, "Query: " + "SELECT * FROM ll_players WHERE " + mode.name().toLowerCase() + " = '" +
        //        sanitize(obj.toString()) + "'");
        try {
            ResultSet res = triplet.getResultSet();
            if (!res.next()) {
                return null;
            } else {
                return new LPlayer(UUID.fromString(res.getString("uuid")),
                        res.getString("name"),
                        res.getInt("claims"),
                        SpigotUtil.exactlocationFromString(res.getString("home")),
                        TimeUtil.stringToTime(res.getString("lastseen")));
            }

        } catch (SQLException e) {
            plugin.getLogger().warning("Error while handling getPlayer!\nError:" + e.getMessage());
        } finally {
            triplet.close();
        }
        return null;
    }

    @Override
    public void savePlayer(IPlayer lp, boolean async) {
        Runnable r = () -> execute("REPLACE INTO ll_players (uuid, name, claims, home, lastseen) VALUES ('" + lp.getUuid() + "', '" +
                lp.getName() + "', " + lp.getClaims() + ", '" +
                SpigotUtil.exactlocationToString(lp.getHome()) + "', '" +
                TimeUtil.timeToString(lp.getLastSeen()) + "')");
        if (async) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    r.run();
                }
            }.runTaskAsynchronously(plugin);
        } else {
            r.run();
        }
    }

}
