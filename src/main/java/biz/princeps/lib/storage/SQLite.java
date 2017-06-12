package biz.princeps.lib.storage;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by spatium on 11.06.17.
 */
public class SQLite extends AbstractDatabase {

    private String dbpath;
    private Connection sqlConnection;


    public SQLite(String dbpath) {
        super();
        this.dbpath = dbpath;
        this.initialize();
    }

    private void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            getLogger().logWarn("The JBDC library for your database type was not found. Please read the plugin's support for more information.");
        }
        Connection conn = getSQLConnection();
        if (conn == null) {
            getLogger().logWarn("Could not establish SQLite Connection");
        }
        this.setupDatabase();
    }

    private Connection getSQLConnection() {
        // Check if Connection was not previously closed.
        try {
            if (sqlConnection == null || sqlConnection.isClosed()) {
                sqlConnection = this.createSQLiteConnection();
            }
        } catch (SQLException e) {
            getLogger().logWarn("Error while attempting to retrieve connection to database: ", e);
        }
        return sqlConnection;
    }

    private Connection createSQLiteConnection() throws SQLException {

        File dbfile = new File(dbpath);
        try {
            if (dbfile.createNewFile()) {
                getLogger().logInfo("Successfully created database file.");
            }
        } catch (IOException e) {
            getLogger().logWarn("Error while creating database file: ", e);
        }
        return DriverManager.getConnection("jdbc:sqlite:" + dbfile);
    }

    @Override
    protected void setupDatabase() {

    }

    @Override
    protected void close() {
        try {
            this.sqlConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void executeUpdate(String query) {
        pool.submit(() -> {
            try (PreparedStatement st = sqlConnection.prepareStatement(query)) {

                st.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected ResultSet executeQuery(String query) {
        try {
            return pool.submit(() -> {
                ResultSet res = null;
                try (PreparedStatement st = sqlConnection.prepareStatement(query)) {

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

