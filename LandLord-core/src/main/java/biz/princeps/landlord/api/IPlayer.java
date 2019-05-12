package biz.princeps.landlord.api;

import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IPlayer {

    int getClaims();

    Location getHome();

    LocalDateTime getLastSeen();

    UUID getUuid();

    String getName();

    void setHome(Location loc);

    void setLastSeen(LocalDateTime now);

    void addClaims(int amount);

}
