package biz.princeps.landlord.persistent;

import biz.princeps.lib.storage_old.annotation.Column;
import biz.princeps.lib.storage_old.annotation.Table;
import biz.princeps.lib.storage_old.annotation.Unique;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
@Table(name = "ll_version")
public class Version {

    @Unique
    @Column(name = "version")
    private int version;

    public static int latestVersion = 3;

    public Version(@Column(name = "version") int version) {
        this.version = version;
    }


}
