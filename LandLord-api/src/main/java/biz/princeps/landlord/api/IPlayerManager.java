package biz.princeps.landlord.api;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

public interface IPlayerManager {

    void add(IPlayer lPlayer);

    void save(IPlayer lp, boolean async);

    void remove(UUID id);

    void saveAllOnlineSync();


    boolean contains(String name);

    IPlayer get(String name);

    IPlayer get(UUID id);

    void getOffline(UUID id, Consumer<IPlayer> consumer);

    void getOffline(String name, Consumer<IPlayer> consumer);


    boolean isInactive(LocalDateTime lastSeenDate);

    void isInactive(UUID id, Consumer<Boolean> consumer);

    void getInactiveRemainingDays(UUID owner, Consumer<Integer> consumer);

    boolean isInactiveSync(UUID id);

    int getInactiveRemainingDaysSync(UUID owner);

    int getInactiveRemainingDays(LocalDateTime date);

    int getMaxClaimPermission(Player player);

    /**
     * Warning, this is blocking! might kill your server.
     *
     * @param id the uuid to get the iplayer from
     * @return the iplayer
     */
    IPlayer getOfflineSync(UUID id);
}
