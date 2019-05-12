package biz.princeps.landlord.persistent;

import biz.princeps.landlord.api.IPlayer;
import biz.princeps.lib.util.SpigotUtil;
import biz.princeps.lib.util.TimeUtil;
import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class LPlayer implements IPlayer {

    private UUID uuid;
    private String name;
    private int claims;
    private Location home;
    private String lastseen;
    private LocalDateTime localDateTime;

    public LPlayer(String uuid, String name, int claims, String home, String lastseen) {
        this.uuid = UUID.fromString(uuid);
        this.name = name;
        this.claims = claims;
        this.home = SpigotUtil.exactlocationFromString(home);
        this.lastseen = lastseen;
        this.localDateTime = TimeUtil.stringToTime(lastseen);
    }

    public LPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getClaims() {
        return claims;
    }

    public void addClaims(int amount) {
        claims += amount;
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public LocalDateTime getLastSeen() {
        return localDateTime;
    }

    public void setLastSeen(LocalDateTime localDateTime) {
        this.lastseen = TimeUtil.timeToString(localDateTime);
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return "LPlayer{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", claims=" + claims +
                ", home=" + home +
                ", lastseen='" + lastseen + '\'' +
                '}';
    }
}
