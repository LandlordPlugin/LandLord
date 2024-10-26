package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMaterialsManager;
import biz.princeps.landlord.util.Skulls;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
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
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta itemMeta = (SkullMeta) skull.getItemMeta();
        itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        skull.setItemMeta(itemMeta);
        return skull;
    }

    @Override
    public ItemStack getPlayerHead(UUID owner, String url) {
        return getPlayerHead(url);
    }

    @Override
    public ItemStack getPlayerHead(OfflinePlayer owner, String url) {
        return getPlayerHead(url);
    }

    @Override
    public ItemStack getPlayerHead(String url) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        profile.setTextures(textures);
        ItemStack stack = ItemStack.of(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setPlayerProfile(profile);
        stack.setItemMeta(meta);
        return stack;
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
