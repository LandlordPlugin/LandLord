package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMaterialsManager;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class MaterialsManager implements IMaterialsManager {

    private final ILandLord plugin;

    public MaterialsManager(ILandLord plugin) {
        this.plugin = plugin;
    }

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
        try {
            return Material.TALL_GRASS;
        } catch (Exception ignored) {
            return Material.valueOf("GRASS");
        }
    }

    @Override
    public ItemStack getPlayerHead(UUID owner) {
        return getPlayerHead(plugin.getServer().getOfflinePlayer(owner));
    }

    @Override
    public ItemStack getPlayerHead(OfflinePlayer owner) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta itemMeta = (SkullMeta) skull.getItemMeta();
        itemMeta.setOwnerProfile(owner.getPlayerProfile());
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

    @Override
    public Material getNetherGrass() {
        return Material.CRIMSON_NYLIUM;
    }

    @Override
    public Material getEnderGrass() {
        return Material.END_STONE;
    }
}
