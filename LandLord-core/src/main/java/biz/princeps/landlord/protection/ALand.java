package biz.princeps.landlord.protection;

import biz.princeps.landlord.api.ILand;
import org.bukkit.World;

public abstract class ALand implements ILand {

    private World world;
    private String name;

    public ALand(World world, String name) {
        this.world = world;
        this.name = name;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public String getName() {
        return name;
    }


}
