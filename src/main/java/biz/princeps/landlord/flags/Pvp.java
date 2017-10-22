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
public class Pvp extends Flag {

    public Pvp(OwnedLand land) {
        super(land);
    }

    public Pvp(OwnedLand land, boolean setDefaultState) {
        super(land, setDefaultState);
    }

    @Override
    public void toggle() {
        ProtectedRegion pr = land.getLand();

        if (pr.getFlags().get(DefaultFlag.PVP) == StateFlag.State.ALLOW) {
            pr.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
            this.status = "DENY";

        } else {
            pr.setFlag(DefaultFlag.PVP, StateFlag.State.ALLOW);
            this.status = "ALLOW";

        }
    }

    @Override
    public Material getMaterial() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public void setDefaultStatus() {
        this.status = land.getLand().getFlags().get(DefaultFlag.PVP).toString().toUpperCase();
    }
}
