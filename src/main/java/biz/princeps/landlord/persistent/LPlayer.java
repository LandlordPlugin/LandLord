package biz.princeps.landlord.persistent;

import biz.princeps.lib.storage.annotation.Column;
import biz.princeps.lib.storage.annotation.Constructor;
import biz.princeps.lib.storage.annotation.Table;
import biz.princeps.lib.storage.annotation.Unique;
import biz.princeps.lib.util.TimeUtil;
import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by spatium on 17.07.17.
 */
@Table(name = "ll_players")
public class LPlayer {

    @Unique
    @Column(name = "uuid", length = 36)
    private UUID uuid;

    @Column(name = "claims")
    private int claims;

    @Column(name = "home")
    private Location home;

    @Column(name = "lastseen", length = 50)
    private String lastseen;
    private LocalDateTime localDateTime;

    @Constructor
    public LPlayer(@Column(name = "uuid") String uuid,
                   @Column(name = "claims") int claims,
                   @Column(name = "home") Location home,
                   @Column(name = "lastseen") String lastseen) {
        this.uuid = UUID.fromString(uuid);
        this.claims = claims;
        this.home = home;
        this.lastseen = lastseen;
        this.localDateTime = TimeUtil.stringToTime(lastseen);
    }

    public LPlayer(UUID uuid) {
        this.uuid = uuid;
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

    public String getLastSeenAsString() {
        return lastseen;
    }
}
