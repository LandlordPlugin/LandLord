package biz.princeps.landlord.persistent;

import biz.princeps.lib.storage.annotation.Column;
import biz.princeps.lib.storage.annotation.Constructor;
import biz.princeps.lib.storage.annotation.Table;
import biz.princeps.lib.storage.annotation.Unique;

import java.util.UUID;

/**
 * Created by spatium on 17.07.17.
 */
@Table(name = "ll_players")
public class LPlayer {

    @Unique
    @Column(name = "uuid", length = 36)
    private UUID uuid;

    @Column(name = "landCount")
    private int landCount;

    @Constructor
    public LPlayer(@Column(name = "uuid") UUID uuid,
                   @Column(name = "landCount") int landCount) {
        this.uuid = uuid;
        this.landCount = landCount;
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

    public int getLandCount() {
        return landCount;
    }

    public void setLandCount(int landCount) {
        this.landCount = landCount;
    }
}
