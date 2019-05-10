package biz.princeps.landlord.persistent;

import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class Offer {

    private String landname;
    private double price;
    private UUID seller;

    public Offer(String landname, double price, UUID seller) {
        this.landname = landname;
        this.price = price;
        this.seller = seller;
    }

    public String getLandname() {
        return landname;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public UUID getSeller() {
        return seller;
    }
}
