package biz.princeps.lib.item;

import biz.princeps.lib.PrincepsLib;
import de.eldoria.eldoutilities.core.EldoUtilities;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Project: PrincepsLib
 * Author: Alex D. (SpatiumPrinceps)
 * <p>
 * Date: 11/7/17 11:48 AM
 */
public class ItemManager {

    private final Map<String, Class<? extends AbstractItem>> items;

    public ItemManager() {
        items = new HashMap<>();
        new ItemActionListener(this);
    }

    public void registerItem(String name, Class<? extends AbstractItem> item) {
        items.put(name, item);
    }

    public AbstractItem getAbstractItem(ItemStack stack) {
        String customItemName = (String) PrincepsLib.crossVersion().getValueFromNBT(stack, "customItemName");

        try {
            Class<? extends AbstractItem> aClass = items.get(customItemName);

            return (AbstractItem) aClass.getConstructors()[0].newInstance();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            EldoUtilities.logger().log(Level.WARNING, "Custom item must implement empty constructor: " + e);
        }
        return null;
    }

    public static ItemStack stack(String string) {
        try {
            Material material = Material.valueOf(string.toUpperCase());
            return new ItemStack(material);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
