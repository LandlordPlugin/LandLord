package biz.princeps.landlord.persistent;

import biz.princeps.landlord.api.IPlayer;
import biz.princeps.lib.storage.Datastorage;
import biz.princeps.lib.storage_old.DatabaseType;
import biz.princeps.lib.util.SpigotUtil;
import biz.princeps.lib.util.TimeUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class Database extends Datastorage {

    private static final int CURRENT_VERSION = 4;

    public Database(Logger logger, DatabaseType type, String hostname, String port, String username, String password,
                    String database) {
        super(logger, type, hostname, port, username, password, database);
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
            logger.warning("Error while handling upgrade!\nError:" + e.getMessage());
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

    /**
     * Fetch an IPlayer out of the database. There are two modes available:
     * 1) uuid
     * 2) name
     * those two modes have to match the column names "uuid" or "name".
     *
     * @param obj  an uuid object or a name string
     * @param mode uuid/name
     * @return the IPlayer if found, if not null
     */
    public IPlayer getPlayer(Object obj, Mode mode) {
        Triplet triplet = executeQuery("SELECT * FROM ll_players WHERE " + mode.name().toLowerCase() + " = '" +
                sanitize(obj.toString()) + "'");
        //System.out.println("Query: " + "SELECT * FROM ll_players WHERE " + mode.name().toLowerCase() + " = '" +
        //       sanitize(obj.toString()) + "'");
        try {
            ResultSet res = triplet.getResultSet();
            if (!res.next()) {
                return null;
            } else {
                return new LPlayer(res.getString("uuid"),
                        res.getString("name"),
                        res.getInt("claims"),
                        res.getString("home"),
                        res.getString("lastseen"));
            }

        } catch (SQLException e) {
            logger.warning("Error while handling getPlayer!\nError:" + e.getMessage());
        } finally {
            triplet.close();
        }
        return null;
    }

    public Collection<IPlayer> getPlayers() {
        Set<IPlayer> playerSet = new HashSet<>();

        Triplet triplet = executeQuery("SELECT * FROM ll_players");
        ResultSet res = triplet.getResultSet();
        try {
            while (res.next()) {
                LPlayer lPlayer = new LPlayer(res.getString("uuid"),
                        res.getString("name"),
                        res.getInt("claims"),
                        res.getString("home"),
                        res.getString("lastseen"));
                playerSet.add(lPlayer);
            }
        } catch (SQLException e) {
            logger.warning("Error while getting all players!\nError: " + e.getMessage());
        }
        return playerSet;
    }

    /**
     * Sanitize the input to avoid sql injections.
     */
    private String sanitize(String input) {
        return input.split(" ")[0].replace(";", "").replace("\\", "")
                .replace("'", "").replace("\"", "");
    }

    /**
     * Saves an IPlayer to the database.
     *
     * @param lp the IPlayer to save
     */
    public void save(IPlayer lp) {
        //System.out.println("Saving... " + lp);
        execute("REPLACE INTO ll_players (uuid, name, claims, home, lastseen) VALUES ('" + lp.getUuid() + "', '" +
                lp.getName() + "', " + lp.getClaims() + ", '" +
                SpigotUtil.exactlocationToString(lp.getHome()) + "', '" +
                TimeUtil.timeToString(lp.getLastSeen()) + "')");
    }

    public enum Mode {
        UUID, NAME
    }
}

