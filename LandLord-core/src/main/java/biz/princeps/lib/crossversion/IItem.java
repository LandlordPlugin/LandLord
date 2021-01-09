package biz.princeps.lib.crossversion;

import org.bukkit.inventory.ItemStack;

/**
 * Project: PrincepsLib
 * Author: Alex D. (SpatiumPrinceps)
 * <p>
 * Date: 11/7/17 11:17 AM
 */
public interface IItem {

    ItemStack addNBTTag(ItemStack stack, String key, Object value);

    Object getValueFromNBT(ItemStack stack, String key);

    boolean hasNBTTag(ItemStack stack, String customItem);
}
