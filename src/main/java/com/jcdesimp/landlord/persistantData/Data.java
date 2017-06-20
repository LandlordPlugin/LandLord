package com.jcdesimp.landlord.persistantData;

/**
 * Created by spatium on 10.06.17.
 */
public class Data {

    private String world;
    private int x, z;

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

    public void setWorld(String world) {
        this.world = world;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
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
}
