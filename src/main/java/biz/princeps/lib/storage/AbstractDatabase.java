package biz.princeps.lib.storage;

import com.jcdesimp.landlord.Landlord;

import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by spatium on 11.06.17.
 */
public abstract class AbstractDatabase {

    protected ExecutorService pool;
    protected Logger logger;

    public AbstractDatabase() {
        this.pool = Executors.newCachedThreadPool();
        logger = Landlord.getInstance().getLogger();
    }

    public Logger getLogger() {
        return logger;
    }

    protected abstract void setupDatabase();

    protected abstract void close();

    protected abstract void executeUpdate(String query);

    protected abstract ResultSet executeQuery(String query);

}
