package biz.princeps.landlord.persistent;

import biz.princeps.lib.storage.Datastorage;
import biz.princeps.lib.storage_old.DatabaseType;
import biz.princeps.lib.util.SpigotUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class Database extends Datastorage {

    private static final int CURRENT_VERSION = 4;

    public Database(Logger logger, DatabaseType type, String hostname, String port, String username, String password, String database) {
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

    private void executeUpgrade(int localVersion) {
        switch (localVersion) {
            case 3:
                //TODO Handle upgrade from old scheme before this patch to v4
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

        execute("CREATE TABLE IF NOT EXISTS ll_advertise(" +
                "landname VARCHAR(36)   NOT NULL," +
                "price DOUBLE           NOT NULL," +
                "seller VARCHAR(36)     NOT NULL," +
                "PRIMARY KEY(landname)" +
                ");");

        execute("CREATE TABLE IF NOT EXISTS ll_version(" +
                "version TINYINT," +
                "PRIMARY KEY(version)" +
                ");");

        execute("INSERT INTO ll_version (version) SELECT " + CURRENT_VERSION + " WHERE NOT EXISTS (SELECT * FROM ll_version)");
    }

    public LPlayer getPlayer(Object obj, Mode mode) {
        ResultSet res = executeQuery("SELECT * FROM ll_players WHERE " + mode.name().toLowerCase() + " = ?", obj);

        try {
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
            try {
                res.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void save(LPlayer lp) {
        System.out.println(lp);
        execute("REPLACE INTO ll_players (uuid, name, claims, home, lastseen) VALUES ('" + lp.getUuid() + "', '" +
                lp.getName() + "', " + lp.getClaims() + ", '" +
                SpigotUtil.exactlocationToString(lp.getHome()) + "', '" + lp.getLastSeenAsString() + "')");
    }

    public Map<String, Offer> fetchOffers() {
        Map<String, Offer> offers = new HashMap<>();
        executeQuery("SELECT * FROM ll_advertise", res -> {
            try {
                while (res.next()) {
                    offers.put(res.getString("landname"), new Offer(res.getString("landname"), res.getDouble("price"), UUID.fromString(res.getString("seller"))));
                }
            } catch (SQLException e) {
                logger.warning("Error while handling fetchOffers!\nError:" + e.getMessage());
            }
        });
        return offers;
    }

    public void save(Offer offer) {
        execute("INSERT INTO ll_advertise (landname, price seller)" +
                "VALUES (?, ?, ?);", offer.getLandname(), offer.getPrice(), offer.getSeller());
    }

    public void remove(String offer) {
        execute("DELETE FROM ll_advertise WHERE landname = ?", offer);
    }

    public enum Mode {
        UUID, NAME
    }
}

