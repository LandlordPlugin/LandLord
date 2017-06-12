package biz.princeps.lib.storage;

import com.mysql.jdbc.log.Slf4JLogger;

import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by spatium on 11.06.17.
 */
public abstract class AbstractDatabase {

    protected ExecutorService pool;
    protected Slf4JLogger logger;

    public AbstractDatabase() {
        this.pool = Executors.newCachedThreadPool();
    }

    public Slf4JLogger getLogger() {
        return logger;
    }

    protected abstract void setupDatabase();

    protected abstract void close();

    protected abstract void executeUpdate(String query);

    protected abstract ResultSet executeQuery(String query);

}
