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
public class Interact extends Flag {


    public Interact(OwnedLand land) {
        super(land);
    }

    public Interact(OwnedLand land, boolean setDefaultState) {
        super(land, setDefaultState);
    }

    @Override
    public void toggle() {
        ProtectedRegion pr = land.getLand();

        if (pr.getFlags().get(DefaultFlag.INTERACT) == StateFlag.State.ALLOW) {

            pr.setFlag(DefaultFlag.INTERACT.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
            pr.setFlag(DefaultFlag.INTERACT, StateFlag.State.DENY);
            this.status = "DENY";

        } else {
            pr.setFlag(DefaultFlag.INTERACT, StateFlag.State.ALLOW);
            this.status = "ALLOW";

        }
    }

    @Override
    public Material getMaterial() {
        return Material.STONE_BUTTON;
    }

    @Override
    public void setDefaultStatus() {
        this.status = land.getLand().getFlags().get(DefaultFlag.INTERACT).toString().toUpperCase();
    }
}
