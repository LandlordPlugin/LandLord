package biz.princeps.landlord.flags;

import biz.princeps.landlord.util.OwnedLand;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 10/22/17
 */
public abstract class Flag implements IFlag {

    protected OwnedLand land;
    protected String status;

    public Flag(OwnedLand land) {
        this(land, false);
    }

    public Flag(OwnedLand land, boolean setDefaultState) {
        this.land = land;

        if (setDefaultState) {
            this.setDefaultStatus();
        } else {
            this.status = "NaN";
        }


    }

    @Override
    public String getStatus() {
        return status;
    }
}
