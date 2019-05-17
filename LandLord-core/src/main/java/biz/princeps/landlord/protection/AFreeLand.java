package biz.princeps.landlord.protection;

import biz.princeps.landlord.api.IFreeLand;
import org.bukkit.World;

public abstract class AFreeLand extends ALand implements IFreeLand {
    public AFreeLand(World world, String name) {
        super(world, name);
    }
}
