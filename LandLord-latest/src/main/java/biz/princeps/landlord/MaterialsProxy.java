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
        return Material.LEGACY_SKULL_ITEM;
    }

    @Override
    public Material getGrass() {
        return Material.GRASS_BLOCK;
    }

    @Override
    public ItemStack getPlayerHead(UUID owner) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta itemMeta = (SkullMeta) skull.getItemMeta();
        itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        skull.setItemMeta(itemMeta);
        return skull;
    }

    @Override
    public ItemStack getLimeWool() {
        return new ItemStack(Material.LIME_WOOL);
    }

    @Override
    public ItemStack getRedWool() {
        return new ItemStack(Material.RED_WOOL);
    }

    @Override
    public Material getFireCharge() {
        return Material.FIRE_CHARGE;
    }
}
