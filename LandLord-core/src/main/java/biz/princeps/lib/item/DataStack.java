package biz.princeps.lib.item;

import biz.princeps.lib.PrincepsLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 * Project: PrincepsLib
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 12/22/17
 */
public class DataStack {

    private Material mat;

    public DataStack(String in) {
        String[] split = in.split(":");

        try {
            this.mat = Material.valueOf(split[0].toUpperCase());

        } catch (NumberFormatException ex) {
            PrincepsLib.getPluginInstance().getLogger().warning("Invalid Material detected!! Value: " + in);
        }
    }

    public DataStack(Material mat) {
        this.mat = mat;
    }

    public Material getMaterial() {
        return mat;
    }

    public ItemStack getItemStack() {
        return new ItemStack(mat, 1, (short) 0);
    }

    public void place(World w, int x, int y, int z) {
        w.getBlockAt(x, y, z).setType(mat);
    }

    public void place(World w, Location loc) {
        w.getBlockAt(loc).setType(mat);
    }
}