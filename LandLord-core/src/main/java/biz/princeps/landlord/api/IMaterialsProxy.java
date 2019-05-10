package biz.princeps.landlord.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface IMaterialsProxy {

    Material getSkull();

    Material getGrass();

    ItemStack getPlayerHead(UUID owner);

    ItemStack getLimeWool();

    ItemStack getRedWool();

    Material getFireCharge();

}
