package biz.princeps.lib.storage;

import biz.princeps.lib.PrincepsLib;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 26/4/18
 */
public class Datastorage {
    protected final Logger logger;
    protected final JavaPlugin pl;
    protected final HikariDataSource ds;

    public Logger getLogger() {
        return logger;
    }

    public Datastorage(Logger logger, String hostname, String port, String username, String password, String database) {
        this.logger = logger;
        this.pl = PrincepsLib.getPluginInstance();

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaxLifetime(600000L);
        hikariConfig.setIdleTimeout(300000L);
        hikariConfig.setLeakDetectionThreshold(300000L);
        hikariConfig.setConnectionTimeout(10000L);

        ds = new HikariDataSource(hikariConfig);
        setupDatabase();
    }

    public void close() {
        ds.close();
    }

    protected Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            logger.warning("Error while trying to pull a new connection: " + e.getMessage());
            return null;
        }
    }

    protected void setupDatabase() {

    }

    public void executeQueryAsync(String query, Consumer<ResultSet> consumer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                executeQuery(query, consumer, null);
            }
        }.runTaskAsynchronously(pl);
    }

    public void executeUpdateAsync(String query, Object... args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                executeUpdate(query, args);
            }
        }.runTaskAsynchronously(pl);
    }

    public void executeAsync(String query, Object... args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                execute(query, args);
            }
        }.runTaskAsynchronously(pl);
    }

    public void executeQueryAsync(String query, Consumer<ResultSet> consumer, Object... args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                executeQuery(query, consumer, args);
            }
        }.runTaskAsynchronously(pl);
    }

    public void executeUpdate(String query, Object... args) {
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(query)) {
            evalutePrepStmt(st, args);
            st.executeUpdate();

        } catch (SQLException e) {
            logger.warning("Error while executing update for query: " + query + "\nError:" + e.getMessage());
        }
    }

    public void execute(String query, Object... args) {
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(query)) {
            evalutePrepStmt(st, args);
            st.execute();

        } catch (SQLException e) {
            logger.warning("Error while executing query: " + query + "\nError:" + e.getMessage());
        }
    }

    public void executeQuery(String query, Consumer<ResultSet> consumer, Object... args) {
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(query)) {
            evalutePrepStmt(st, args);
            ResultSet res = st.executeQuery();
            consumer.accept(res);
            res.close();

        } catch (SQLException e) {
            logger.warning("Error while getting result set for query: " + query + "\nError:" + e.getMessage());
        }
    }

    public Triplet executeQuery(String query, Object... args) {
        Connection con = getConnection();
        PreparedStatement st;
        try {
            st = con.prepareStatement(query);
            evalutePrepStmt(st, args);
            ResultSet res = st.executeQuery();
            return new Triplet(con, st, res);
        } catch (SQLException e) {
            logger.warning("Error while getting result set for query: " + query + "\nError:" + e.getMessage());
        }
        // Connection should be closed via the result set
        return null;
    }

    private void evalutePrepStmt(PreparedStatement st, Object... args) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            Object obj = args[i];
            if (obj instanceof String)
                st.setString(i + 1, (String) obj);
            else if (obj instanceof Integer)
                st.setInt(i + 1, (int) obj);
            else if (obj instanceof Double)
                st.setDouble(i + 1, (double) obj);
            else if (obj instanceof Float)
                st.setFloat(i + 1, (float) obj);
            else if (obj instanceof Boolean)
                st.setBoolean(i + 1, (boolean) obj);
            else if (obj instanceof Long)
                st.setLong(i + 1, (long) obj);
            else
                st.setNull(i + 1, Types.VARCHAR);
        }
    }

    public static class Triplet {
        final Connection con;
        final PreparedStatement pr;
        final ResultSet res;

        public Triplet(Connection con, PreparedStatement pr, ResultSet res) {
            this.con = con;
            this.pr = pr;
            this.res = res;
        }

        public ResultSet getResultSet() {
            return res;
        }

        public void close() {
            try {
                res.close();
                pr.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
