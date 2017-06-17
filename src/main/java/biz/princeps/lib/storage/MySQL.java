package biz.princeps.lib.storage;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.Data;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import com.jcdesimp.landlord.persistantData.db.SQLiteDatabase;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by spatium on 11.06.17.
 */
public abstract class MySQL extends AbstractDatabase {

    protected HikariDataSource ds;

    public MySQL(String hostname, int port, String database, String username, String password) {
        super();
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(MysqlDataSource.class.getName());

        config.addDataSourceProperty("serverName", hostname);
        config.addDataSourceProperty("port", port);
        config.addDataSourceProperty("databaseName", database);
        config.addDataSourceProperty("user", username);
        config.addDataSourceProperty("password", password);

        ds = new HikariDataSource(config);
    }

    public void setupDatabase() {

    }

    protected Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void close() {
        ds.close();
    }

    public void executeUpdate(String query) {
        pool.submit(() -> {
            try (Connection con = ds.getConnection();
                 PreparedStatement st = con.prepareStatement(query)) {

                st.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
    }

    public void execute(String query) {
        pool.submit(() -> {
            try (Connection con = ds.getConnection();
                 PreparedStatement st = con.prepareStatement(query)) {

                st.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
    }

    public ResultSet executeQuery(String query) {
        try {
            return pool.submit(() -> {
                ResultSet res = null;
                try (Connection con = ds.getConnection();
                     PreparedStatement st = con.prepareStatement(query)) {

                    res = st.executeQuery();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return res;

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

}
