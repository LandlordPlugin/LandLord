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
@Table(name = "ll_advertise")
public class Offers {

    @Unique
    @Column(name = "landname", length = 36)
    private String landname;

    @Column(name = "price")
    private double price;

    @Column(name = "seller", length = 36)
    private UUID seller;

    public Offers(@Column(name = "landname") String landname,
                  @Column(name = "price") double price,
                  @Column(name = "seller") UUID seller) {
        this.landname = landname;
        this.price = price;
        this.seller = seller;
    }


    public void setPrice(double price) {
        this.price = price;
    }

    public String getLandname() {
        return landname;
    }

    public double getPrice() {
        return price;
    }

    public UUID getSeller() {
        return seller;
    }
}
