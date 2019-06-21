package biz.princeps.landlord.api;

import org.bukkit.Material;
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

}
