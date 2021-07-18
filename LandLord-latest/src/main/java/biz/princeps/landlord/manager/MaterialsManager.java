package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.IMaterialsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class MaterialsManager implements IMaterialsManager {
    @Override
    public Material getSkull() {
        return Material.PLAYER_HEAD;
    }

    @Override
    public Material getGrass() {
        return Material.GRASS_BLOCK;
    }

    @Override
    public Material getLongGrass() {
        return Material.GRASS;
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
    public ItemStack getWitherSkull() {
        return new ItemStack(Material.WITHER_SKELETON_SKULL);
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

    @Override
    public ItemStack getGreyStainedGlass() {
        return new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    }
}
