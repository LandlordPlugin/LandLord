package biz.princeps.landlord.manager;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.persistent.Database;
import biz.princeps.landlord.persistent.LPlayer;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class LPlayerManager {

    private Map<UUID, LPlayer> players;

    private Database db;
    private Landlord plugin;

    public LPlayerManager(Database db) {
        this.players = new HashMap<>();
        this.plugin = Landlord.getInstance();
        this.db = db;
    }

    public void add(LPlayer lPlayer) {
        this.players.put(lPlayer.getUuid(), lPlayer);
    }

    public void saveAsync(LPlayer lp) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> saveSync(lp));
    }

    public void saveSync(LPlayer lp) {
        db.save(lp);
    }

    public void remove(UUID id) {
        players.remove(id);
    }

    public void getOfflinePlayerAsync(UUID uuid, Consumer<LPlayer> consumer) {
        plugin.getExecutorService().execute(() -> consumer.accept(db.getPlayer(uuid, Database.Mode.UUID)));
    }

    public void getOfflinePlayerAsync(String name, Consumer<LPlayer> consumer) {
        plugin.getExecutorService().execute(() -> consumer.accept(db.getPlayer(name, Database.Mode.NAME)));
    }

    public LPlayer getOfflinePlayerSync(UUID uuid) {
        return db.getPlayer(uuid, Database.Mode.UUID);
    }

    public LPlayer getOfflinePlayerSync(String name) {
        return db.getPlayer(name, Database.Mode.NAME);
    }

    public boolean contains(String name) {
        for (LPlayer lPlayer : this.players.values()) {
            if (lPlayer.getName() != null && lPlayer.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public LPlayer get(String name) {
        for (LPlayer lPlayer : this.players.values()) {
            if (lPlayer.getName() != null && lPlayer.getName().equals(name)) {
                return lPlayer;
            }
        }
        return null;
    }

    public LPlayer get(UUID id) {
        return this.players.get(id);
    }

    /**
     * Measures if a player is inactive based on the date he was seen the last time.
     * If this date + the timegate is before right now, he is inactive
     *
     * @param lastSeenDate the date the player was last seen
     * @return if the player is inactive or not
     */
    public boolean isInactive(LocalDateTime lastSeenDate) {
        if (!Options.enabled_inactiveBuyUp()) return false;

        if (lastSeenDate == null) {
            return false;
        }

        int days = plugin.getConfig().getInt("BuyUpInactive.timegate");

        // yes, this guy is inactive
        return lastSeenDate.plusDays(days).isBefore(LocalDateTime.now());
    }

    public void isInactive(UUID id, Consumer<Boolean> consumer) {
        plugin.getExecutorService().execute(() -> consumer.accept(isInactive(id)));
    }

    /**
     * Warning, this method might cause lag if done on the main thread!
     *
     * @param id the uuid which should be checked
     * @return if the given id is marked as inactive
     */
    public Boolean isInactive(UUID id) {
        LPlayer lPlayer = db.getPlayer(id, Database.Mode.UUID);
        if (lPlayer != null) {
            return isInactive(lPlayer.getLastSeen());
        }
        return false;
    }

    public void getInactiveRemainingDays(UUID owner, Consumer<Integer> consumer) {
        plugin.getExecutorService().execute(() -> consumer.accept(getInactiveRemainingDays(owner)));
    }

    /**
     * Warning, this method might cause lag if done on the main thread!
     *
     * @param owner the uuid which should be checked
     * @return the amount of days, which are missing until the player become inactive
     */
    public int getInactiveRemainingDays(UUID owner) {

        long days = plugin.getConfig().getInt("BuyUpInactive.timegate");
        LPlayer lPlayer = db.getPlayer(owner, Database.Mode.UUID);
        if (lPlayer != null) {
            return (int) (days - (Duration.between(LocalDateTime.now(), lPlayer.getLastSeen()).toDays()));
        }
        return -1;
    }
}
