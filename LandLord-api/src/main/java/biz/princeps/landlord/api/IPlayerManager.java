package biz.princeps.landlord.api;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IPlayerManager {

    void add(IPlayer lPlayer);

    void save(IPlayer lp, boolean async);

    void remove(UUID id);

    void saveAllOnlineSync();


    boolean contains(String name);

    IPlayer get(String name);

    IPlayer get(UUID id);

    IPlayer getOffline(UUID id);

    IPlayer getOffline(String name);


    boolean isInactive(LocalDateTime lastSeenDate);

    boolean isInactive(UUID id);

    int getInactiveRemainingDays(UUID owner);


    int getMaxClaimPermission(Player player);
}
