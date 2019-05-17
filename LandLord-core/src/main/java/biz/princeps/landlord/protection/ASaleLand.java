package biz.princeps.landlord.protection;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ISaleLandLand;
import org.bukkit.World;

public abstract class ASaleLand extends APossessedLand implements ISaleLandLand {

    private double price;

    public ASaleLand(ILandLord pl, World world, String name, double price) {
        super(pl, world, name);
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }
}
