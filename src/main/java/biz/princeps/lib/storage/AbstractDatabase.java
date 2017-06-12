package biz.princeps.lib.storage;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.Data;
import com.jcdesimp.landlord.persistantData.Friend;
import com.jcdesimp.landlord.persistantData.LandFlag;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;
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

    public abstract void close();

    protected abstract void executeUpdate(String query);

    protected abstract ResultSet executeQuery(String query);

    protected abstract void execute(String query);


    public abstract OwnedLand getLand(Data data);

    protected abstract List<Friend> getFriends(int id);

    protected abstract List<LandFlag> getFlags(int landid);

    public abstract void removeFriend(UUID f);

    public abstract void removeLand(int landid);

    public abstract void save(OwnedLand land);

    public abstract List<OwnedLand> getLands(UUID owner);

    public abstract List<OwnedLand> getLands(UUID owner, String world);

    public abstract List<OwnedLand> getLands(String world);

    public abstract List<OwnedLand> getNearbyLands(Location location, int offsetX, int offsetZ);

    public abstract int getFirstFreeLandID();

    public abstract int getFirstFreeFlagID();

}
