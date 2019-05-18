package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IPlayerManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.persistent.FlatFileStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class LPlayerManager implements IPlayerManager {

    private Map<UUID, IPlayer> players;

    private ILandLord plugin;
    private BukkitScheduler scheduler;

    private FlatFileStorage stor;


    public LPlayerManager(ILandLord pl) {
        this.players = new HashMap<>();
        this.plugin = pl;
        this.stor = new FlatFileStorage(pl.getPlugin());
        this.stor.init();

        this.scheduler = plugin.getPlugin().getServer().getScheduler();
    }

    @Override
    public void add(IPlayer lPlayer) {
        this.players.put(lPlayer.getUuid(), lPlayer);
    }


    @Override
    public void save(IPlayer lp, boolean b) {
        this.stor.savePlayer(lp, true);
    }

    @Override
    public void saveAllOnlineSync() {
        for (IPlayer value : players.values()) {
            save(value, false);
        }
        stor.save();
    }


    @Override
    public void remove(UUID id) {
        players.remove(id);
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

    @Override
    public IPlayer getOffline(UUID id) {
        return stor.getPlayer(id);
    }

    @Override
    public IPlayer getOffline(String name) {
        return getOffline(Bukkit.getOfflinePlayer(name).getUniqueId());
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
    public boolean isInactive(UUID id) {
        return isInactive(getOffline(id).getLastSeen());
    }


    @Override
    public int getInactiveRemainingDays(UUID owner) {
        long days = plugin.getConfig().getInt("BuyUpInactive.timegate");
        IPlayer offline = getOffline(owner);
        if (offline != null) {
            return (int) (days - (Duration.between(offline.getLastSeen(), LocalDateTime.now()).toDays()));
        }
        return -1;
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
