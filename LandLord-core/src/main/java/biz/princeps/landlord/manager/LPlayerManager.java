package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IPlayerManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.api.exceptions.PlayerOfflineException;
import biz.princeps.landlord.persistent.Database;
import biz.princeps.landlord.persistent.LPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class LPlayerManager implements IPlayerManager {

    private Map<UUID, IPlayer> players;

    private Database db;
    private ILandLord plugin;
    private BukkitScheduler scheduler;


    public LPlayerManager(Database db, ILandLord pl) {
        this.players = new HashMap<>();
        this.plugin = pl;
        this.db = db;
        this.scheduler = plugin.getPlugin().getServer().getScheduler();
    }

    @Override
    public void add(IPlayer lPlayer) {
        this.players.put(lPlayer.getUuid(), lPlayer);
    }

    @Override
    public void saveAsync(IPlayer lp) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin.getPlugin(), () -> saveSync(lp));
    }

    @Override
    public void saveSync(IPlayer lp) {
        db.save(lp);
    }

    @Override
    public void remove(UUID id) {
        players.remove(id);
    }

    @Override
    public void getOfflinePlayerAsync(UUID uuid, Consumer<IPlayer> consumer) {
        scheduler.runTaskAsynchronously(plugin.getPlugin(),
                () -> consumer.accept(db.getPlayer(uuid, Database.Mode.UUID)));
    }

    @Override
    public void getOfflinePlayerAsync(String name, Consumer<IPlayer> consumer) {
        scheduler.runTaskAsynchronously(plugin.getPlugin(),
                () -> consumer.accept(db.getPlayer(name, Database.Mode.NAME)));
    }

    @Override
    public IPlayer getOfflinePlayerSync(UUID uuid) {
        return db.getPlayer(uuid, Database.Mode.UUID);
    }

    @Override
    public IPlayer getOfflinePlayerSync(String name) {
        return db.getPlayer(name, Database.Mode.NAME);
    }

    @Override
    public boolean contains(String name) {
        for (IPlayer lPlayer : this.players.values()) {
            if (lPlayer.getName() != null && lPlayer.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IPlayer get(String name) {
        for (IPlayer lPlayer : this.players.values()) {
            if (lPlayer.getName() != null && lPlayer.getName().equals(name)) {
                return lPlayer;
            }
        }
        return null;
    }

    @Override
    public IPlayer get(UUID id) {
        return this.players.get(id);
    }

    /**
     * Measures if a player is inactive based on the date he was seen the last time.
     * If this date + the timegate is before right now, he is inactive
     *
     * @param lastSeenDate the date the player was last seen
     * @return if the player is inactive or not
     */
    @Override
    public boolean isInactive(LocalDateTime lastSeenDate) {
        if (!Options.enabled_inactiveBuyUp()) return false;

        if (lastSeenDate == null) {
            return false;
        }

        int days = plugin.getConfig().getInt("BuyUpInactive.timegate");

        // yes, this guy is inactive
        return lastSeenDate.plusDays(days).isBefore(LocalDateTime.now());
    }

    @Override
    public void isInactive(UUID id, Consumer<Boolean> consumer) {
        scheduler.runTaskAsynchronously(plugin.getPlugin(), () -> consumer.accept(isInactive(id)));
    }

    /**
     * Warning, this method might cause lag if done on the main thread!
     *
     * @param id the uuid which should be checked
     * @return if the given id is marked as inactive
     */
    @Override
    public Boolean isInactive(UUID id) {
        LPlayer lPlayer = (LPlayer) db.getPlayer(id, Database.Mode.UUID);
        if (lPlayer != null) {
            return isInactive(lPlayer.getLastSeen());
        }
        return false;
    }

    @Override
    public void getInactiveRemainingDays(UUID owner, Consumer<Integer> consumer) {
        scheduler.runTaskAsynchronously(plugin.getPlugin(), () -> consumer.accept(getInactiveRemainingDays(owner)));
    }

    /**
     * Warning, this method might cause lag if done on the main thread!
     *
     * @param owner the uuid which should be checked
     * @return the amount of days, which are missing until the player become inactive
     */
    @Override
    public int getInactiveRemainingDays(UUID owner) {

        long days = plugin.getConfig().getInt("BuyUpInactive.timegate");
        LPlayer lPlayer = (LPlayer) db.getPlayer(owner, Database.Mode.UUID);
        if (lPlayer != null) {
            return (int) (days - (Duration.between(lPlayer.getLastSeen(), LocalDateTime.now()).toDays()));
        }
        return -1;
    }

    @Override
    public IPlayer getOnlinePlayer(UUID id) throws PlayerOfflineException {
        if (get(id) == null) {
            throw new PlayerOfflineException();
        }
        return get(id);
    }

    @Override
    public void getOfflinePlayer(UUID id, Consumer<IPlayer> consumer) {
        getOfflinePlayerAsync(id, consumer);
    }

    @Override
    public int getMaxClaimPermission(Player player) {
        List<Integer> limitlist = plugin.getConfig().getIntegerList("limits");

        if (!player.hasPermission("landlord.limit.override")) {
            // We need to find out, whats the maximum limit.x permission is a player has

            int highestAllowedLandCount = -1;
            for (Integer integer : limitlist) {
                if (player.hasPermission("landlord.limit." + integer)) {
                    highestAllowedLandCount = integer;
                }
            }
            return highestAllowedLandCount;
        } else {
            return Integer.MIN_VALUE;
        }
    }
}
