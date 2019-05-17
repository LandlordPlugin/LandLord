package biz.princeps.landlord.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface IMaterialsManager {

    Material getSkull();

    Material getGrass();

    ItemStack getPlayerHead(UUID owner);

    ItemStack getWitherSkull();

    ItemStack getLimeWool();

    ItemStack getRedWool();

    Material getFireCharge();

    ItemStack getGreyStainedGlass();

}
