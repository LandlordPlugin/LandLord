package com.jcdesimp.landlord.persistantData;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Created by spatium on 10.06.17.
 */
public class Data {

    private final String world;
    private final int x, z;

    public Data(String world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }


    @Override
    public String toString() {
        return "[" + world + ":" + x + ":" + z + "]";
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Data) {
            Data d = (Data) object;
            if (d.getWorld().equals(world) && d.getX() == x && d.getZ() == z)
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(world)
                .append(x)
                .append(z).toHashCode();
    }


}
