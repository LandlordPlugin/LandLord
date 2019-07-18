package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.*;
import biz.princeps.landlord.persistent.FlatFileStorage;
import biz.princeps.landlord.persistent.SQLStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class LPlayerManager implements IPlayerManager {

    private Map<UUID, IPlayer> players;

    private ILandLord plugin;
    private IStorage stor;


    public LPlayerManager(ILandLord pl) {
        this.players = new HashMap<>();
        this.plugin = pl;

        if (pl.getConfig().getString("DatabaseType").equalsIgnoreCase("MySQL")) {
            this.stor = new SQLStorage(plugin.getPlugin());
        } else {
            this.stor = new FlatFileStorage(pl.getPlugin());
            ((FlatFileStorage) this.stor).init();
        }
    }

    @Override
    public synchronized void add(IPlayer lPlayer) {
        this.players.put(lPlayer.getUuid(), lPlayer);
    }


    @Override
    public synchronized void save(IPlayer lp, boolean b) {
        this.stor.savePlayer(lp, b);
    }

    @Override
    public void saveAllOnlineSync() {
        for (IPlayer value : players.values()) {
            save(value, false);
        }
        if (this.stor instanceof FlatFileStorage) {
            ((FlatFileStorage) stor).save();
        }
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
    public void getOffline(UUID id, Consumer<IPlayer> consumer) {
        stor.getPlayer(id, consumer);
    }

    @Override
    public void getOffline(String name, Consumer<IPlayer> consumer) {
        getOffline(Bukkit.getOfflinePlayer(name).getUniqueId(), consumer);
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
        getOffline(id, (lp) -> {
            if (lp != null) {
                consumer.accept(isInactive(lp.getLastSeen()));
            } else {
                consumer.accept(false);
            }
        });
    }


    @Override
    public void getInactiveRemainingDays(UUID owner, Consumer<Integer> consumer) {
        long days = plugin.getConfig().getInt("BuyUpInactive.timegate");
        this.getOffline(owner, (offline) -> {
            if (offline != null) {
                consumer.accept((int) (days - (Duration.between(offline.getLastSeen(), LocalDateTime.now()).toDays())));
            } else {
                consumer.accept(-1);
            }
        });
    }

    @Override
    public boolean isInactiveSync(UUID id) {
        IPlayer lp = getOfflineSync(id);
        if (lp != null) {
            return isInactive(lp.getLastSeen());
        } else {
            return false;
        }

    }

    @Override
    public int getInactiveRemainingDaysSync(UUID owner) {
        long days = plugin.getConfig().getInt("BuyUpInactive.timegate");

        IPlayer offline = getOfflineSync(owner);

        if (offline != null) {
            return (int) (days - (Duration.between(offline.getLastSeen(), LocalDateTime.now()).toDays()));
        } else {
            return -1;
        }
    }

    @Override
    public int getInactiveRemainingDays(LocalDateTime date) {
        long days = plugin.getConfig().getInt("BuyUpInactive.timegate");
        return (int) (days - (Duration.between(date, LocalDateTime.now()).toDays()));
    }


    @Override
    public int getMaxClaimPermission(Player player) {
        if (!player.hasPermission("landlord.limit.override")) {
            // We need to find out, whats the maximum limit.x permission is a player has

            int highestAllowedLandCount = -1;
            Set<PermissionAttachmentInfo> perms = player.getEffectivePermissions();
            for (PermissionAttachmentInfo perm : perms) {
                if (perm.getValue()) {
                    String s = perm.getPermission();
                    if (s.startsWith("landlord.limit.")) {
                        int value = Integer.parseInt(s.substring(s.lastIndexOf('.') + 1));
                        if (value > highestAllowedLandCount) {
                            highestAllowedLandCount = value;
                        }
                    }
                }
            }
            return highestAllowedLandCount;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public IPlayer getOfflineSync(UUID id) {
        return stor.getPlayer(id);
    }
}
