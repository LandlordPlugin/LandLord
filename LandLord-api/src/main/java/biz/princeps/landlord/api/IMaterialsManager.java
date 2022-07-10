package biz.princeps.landlord.api;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Handles Materials, that changed during major minecraft version upgrades.
 */
public interface IMaterialsManager {

    Material getSkull();

    Material getGrass();

    Material getLongGrass();

    ItemStack getPlayerHead(UUID owner);

    ItemStack getWitherSkull();

    ItemStack getLimeWool();

    ItemStack getRedWool();

    Material getFireCharge();

    ItemStack getGreyStainedGlass();

    Material getNetherGrass();

    Material getEnderGrass();

    default Material getWorldGrass(World world) {
        switch (world.getEnvironment()) {
            case NORMAL:
                return getGrass();
            case NETHER:
                return getNetherGrass();
            case THE_END:
                return getEnderGrass();
            default:
                return Material.BARRIER;
        }
    }

}
