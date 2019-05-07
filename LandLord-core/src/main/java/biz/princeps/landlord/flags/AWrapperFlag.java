package biz.princeps.landlord.flags;

import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWrapperFlag;
import org.bukkit.Material;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 07/05/19
 */
public abstract class AWrapperFlag implements IWrapperFlag {

    protected IOwnedLand land;
    protected String status;
    protected Material mat;

    public AWrapperFlag(IOwnedLand land, String status, Material mat) {
        this.land = land;
        this.mat = mat;
        this.status = status;
    }


    public Material getMaterial() {
        return mat;
    }

    public String getStatus() {
        return status;
    }
}
