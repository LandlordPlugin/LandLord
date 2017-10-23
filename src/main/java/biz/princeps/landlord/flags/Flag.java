package biz.princeps.landlord.flags;

import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 10/22/17
 */
public class Flag implements IFlag {

    protected OwnedLand land;
    protected String status;
    protected StateFlag flag;
    protected Material mat;

    public Flag(StateFlag flag, OwnedLand land, Material mat) {
        this(flag, land, mat, false);
    }

    public Flag(StateFlag flag, OwnedLand land, Material mat, boolean setDefaultState) {
        this.land = land;
        this.flag = flag;
        if (setDefaultState) {
            this.setDefaultStatus();
        } else {
            this.status = "NaN";
        }
    }

    @Override
    public void toggle() {
        ProtectedRegion pr = land.getWGLand();

        if (pr.getFlags().get(flag) == StateFlag.State.ALLOW) {

            pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
            pr.setFlag(flag, StateFlag.State.DENY);
            this.status = "DENY";

        } else {
            pr.setFlag(flag, StateFlag.State.ALLOW);
            this.status = "ALLOW";

        }
    }

    @Override
    public Material getMaterial() {
        return null;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setDefaultStatus() {

    }
}
