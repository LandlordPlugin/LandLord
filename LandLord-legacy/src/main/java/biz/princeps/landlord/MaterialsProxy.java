package biz.princeps.landlord;

import biz.princeps.landlord.api.IMaterialsProxy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class MaterialsProxy implements IMaterialsProxy {
    @Override
    public Material getSkull() {
        return Material.SKULL_ITEM;
    }

    @Override
    public Material getGrass() {
        return Material.GRASS;
    }

    @Override
    public ItemStack getPlayerHead(UUID owner) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM);
        SkullMeta itemMeta = (SkullMeta) skull.getItemMeta();
        itemMeta.setOwner(Bukkit.getOfflinePlayer(owner).getName());
        skull.setItemMeta(itemMeta);
        return skull;
    }

    @Override
    public ItemStack getLimeWool() {
        return new ItemStack(Material.WOOL, 1, (short) 5);
    }

    @Override
    public ItemStack getRedWool() {
        return new ItemStack(Material.WOOL, 1, (short) 14);
    }

    @Override
    public Material getFireCharge() {
        return Material.FIREBALL;
    }


}
