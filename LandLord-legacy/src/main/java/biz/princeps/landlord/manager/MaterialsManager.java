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
        return Material.SKULL_ITEM;
    }

    @Override
    public Material getGrass() {
        return Material.GRASS;
    }

    @Override
    public Material getLongGrass() {
        return Material.LONG_GRASS;
    }

    @Override
    public ItemStack getPlayerHead(UUID owner) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM);
        SkullMeta itemMeta = (SkullMeta) skull.getItemMeta();
        itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        skull.setItemMeta(itemMeta);
        return skull;
    }

    @Override
    public ItemStack getWitherSkull() {
        return new ItemStack(Material.SKULL_ITEM, 1, (short) 1);
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

    @Override
    public ItemStack getGreyStainedGlass() {
        return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
    }


}
