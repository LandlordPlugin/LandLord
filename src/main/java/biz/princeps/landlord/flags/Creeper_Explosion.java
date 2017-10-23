package biz.princeps.landlord.flags;

import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 10/22/17
 */
public class Creeper_Explosion extends Flag {


    public Creeper_Explosion(OwnedLand land) {
        super(land);
    }

    public Creeper_Explosion(OwnedLand land, boolean setDefaultState) {
        super(land, setDefaultState);
    }

    @Override
    public void toggle() {
        ProtectedRegion pr = land.getWGLand();

        if (pr.getFlags().get(DefaultFlag.CREEPER_EXPLOSION) == StateFlag.State.ALLOW) {

            pr.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
            this.status = "DENY";

        } else {
            pr.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.ALLOW);
            this.status = "ALLOW";
        }
    }

    @Override
    public Material getMaterial() {
        return Material.TNT;
    }

    @Override
    public void setDefaultStatus() {
        this.status = land.getWGLand().getFlags().get(DefaultFlag.CREEPER_EXPLOSION).toString().toUpperCase();
    }

}
