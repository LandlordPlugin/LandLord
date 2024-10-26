package biz.princeps.lib.crossversion;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 * Created by spatium on 28.07.17.
 */
public class CrossVersion {

    private final IItem item;

    public CrossVersion() {
        this.item = new Item();
    }

    public static String getVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    public ItemStack addNBTTag(ItemStack stack, String key, Object value) {
        return item.addNBTTag(stack, key, value);
    }

    public Object getValueFromNBT(ItemStack stack, String key) {
        return item.getValueFromNBT(stack, key);
    }

    public boolean hasNBTTag(ItemStack stack, String customItem) {
        return item.hasNBTTag(stack, customItem);
    }

}
