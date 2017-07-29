package biz.princeps.landlord.persistent;

import biz.princeps.lib.storage.annotation.Column;
import biz.princeps.lib.storage.annotation.Constructor;
import biz.princeps.lib.storage.annotation.Table;
import biz.princeps.lib.storage.annotation.Unique;
import org.bukkit.Location;

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

    @Constructor
    public LPlayer(@Column(name = "uuid") String uuid,
                   @Column(name = "claims") int claims,
                   @Column(name = "home") Location home) {
        this.uuid = UUID.fromString(uuid);
        this.claims = claims;
        this.home = home;
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
}
