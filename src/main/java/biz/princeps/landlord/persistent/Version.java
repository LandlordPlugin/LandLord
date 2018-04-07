package biz.princeps.landlord.persistent;

import biz.princeps.lib.storage.annotation.Column;
import biz.princeps.lib.storage.annotation.Table;
import biz.princeps.lib.storage.annotation.Unique;

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
