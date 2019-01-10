package biz.princeps.landlord.api;

import biz.princeps.landlord.api.exceptions.PlayerOfflineException;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public interface IPlayerManager {

    IPlayer getOnlinePlayer(UUID id) throws PlayerOfflineException;

    void getOfflinePlayer(UUID id, Consumer<IPlayer> consumer);

    int getMaxClaimPermission(Player player);
}
