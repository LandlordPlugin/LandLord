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
public class Chest_Access extends Flag {

    public Chest_Access(OwnedLand land) {
        super(land);
    }

    public Chest_Access(OwnedLand land, boolean setDefaultState) {
        super(land, setDefaultState);
    }

    @Override
    public void toggle() {
        ProtectedRegion pr = land.getWGLand();

        if (pr.getFlags().get(DefaultFlag.CHEST_ACCESS) == StateFlag.State.ALLOW) {

            pr.setFlag(DefaultFlag.CHEST_ACCESS.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
            pr.setFlag(DefaultFlag.CHEST_ACCESS, StateFlag.State.DENY);
            this.status = "DENY";

        } else {
            pr.setFlag(DefaultFlag.CHEST_ACCESS, StateFlag.State.ALLOW);
            this.status = "ALLOW";

        }
    }

    @Override
    public Material getMaterial() {
        return Material.CHEST;
    }

    @Override
    public void setDefaultStatus() {
        this.status = land.getWGLand().getFlags().get(DefaultFlag.CHEST_ACCESS).toString().toUpperCase();
    }
}
