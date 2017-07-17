package biz.princeps.landlord.persistent;

import biz.princeps.lib.storage.annotation.Column;
import biz.princeps.lib.storage.annotation.Table;

/**
 * Created by spatium on 17.07.17.
 */
@Table(name = "land")
public class Land {

    @Column(name = "world", length = 16)
    private String world;

    @Column(name = "x")
    private int x;

    @Column(name = "z")
    private int z;


}
