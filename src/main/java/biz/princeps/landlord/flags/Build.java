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
public class Build extends Flag {


    public Build(OwnedLand land) {
        super(land);
    }

    public Build(OwnedLand land, boolean setDefaultState) {
        super(land, setDefaultState);
    }

    @Override
    public void toggle() {
        ProtectedRegion pr = land.getWGLand();

        if (pr.getFlags().get(DefaultFlag.BUILD) == StateFlag.State.ALLOW) {
            // Switch flag to: members are allowed
            // => Disable build for nonmembers

            pr.setFlag(DefaultFlag.BUILD.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
            pr.setFlag(DefaultFlag.BUILD, StateFlag.State.DENY);
            this.status = "DENY";

        } else {
            pr.setFlag(DefaultFlag.BUILD, StateFlag.State.ALLOW);
            this.status = "ALLOW";
        }
    }

    @Override
    public Material getMaterial() {
        return Material.GRASS;
    }

    @Override
    public void setDefaultStatus() {
        this.status = land.getWGLand().getFlags().get(DefaultFlag.BUILD).toString().toUpperCase();
    }

}
