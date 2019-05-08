package biz.princeps.landlord.api;

import biz.princeps.landlord.api.exceptions.PlayerOfflineException;
import biz.princeps.landlord.persistent.LPlayer;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

public interface IPlayerManager {

    void add(LPlayer lPlayer);

    void saveAsync(LPlayer lp);

    void saveSync(LPlayer lp);

    void remove(UUID id);

    void getOfflinePlayerAsync(UUID uuid, Consumer<IPlayer> consumer);

    void getOfflinePlayerAsync(String name, Consumer<IPlayer> consumer);

    IPlayer getOfflinePlayerSync(UUID uuid);

    IPlayer getOfflinePlayerSync(String name);

    boolean contains(String name);

    LPlayer get(String name);

    LPlayer get(UUID id);

    boolean isInactive(LocalDateTime lastSeenDate);

    void isInactive(UUID id, Consumer<Boolean> consumer);

    Boolean isInactive(UUID id);

    void getInactiveRemainingDays(UUID owner, Consumer<Integer> consumer);

    int getInactiveRemainingDays(UUID owner);

    IPlayer getOnlinePlayer(UUID id) throws PlayerOfflineException;

    void getOfflinePlayer(UUID id, Consumer<IPlayer> consumer);

    int getMaxClaimPermission(Player player);
}
