package biz.princeps.lib.item;

import biz.princeps.lib.PrincepsLib;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Project: PrincepsLib
 * Author: Alex D. (SpatiumPrinceps)
 * <p>
 * Date: 11/7/17 10:51 AM
 */
public abstract class AbstractItem {

    protected final String name;
    private final boolean breakBlocks;
    private ItemStack stack;
    private boolean glowing;

    /**
     * Used to initially create a custom item stack
     *
     * @param name    name, which should be saved inside nbt and will also be used to get the instance of this class
     * @param stack   the item stack, which should be wrapped
     * @param glowing if the item should be glowing or not
     */
    public AbstractItem(String name, ItemStack stack, boolean glowing, boolean breakBlocks) {
        this.name = name;
        this.stack = PrincepsLib.crossVersion().addNBTTag(stack, "customItem", "true");
        this.stack = PrincepsLib.crossVersion().addNBTTag(stack, "customItemName", name);
        setGlowing(glowing);
        this.breakBlocks = breakBlocks;
    }

    public static boolean isCustomItem(ItemStack stack) {
        return PrincepsLib.crossVersion().hasNBTTag(stack, "customItem");
    }

    public void give(Player p) {
        p.getInventory().addItem(stack);
    }

    /**
     * Called when the player clicks with this item
     *
     * @param action the action, leftclickblock, rightclickblock...
     * @param player the clicking player
     * @param loc    the location clicked on. may be null!
     */
    public abstract void onClick(Action action, Player player, Location loc);

    public void setStackSize(int size) {
        stack.setAmount(size);
    }

    public void setGlowing(boolean glow) {
        this.glowing = glow;
        if (glowing) {
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            stack.setItemMeta(itemMeta);
        } else {
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.removeEnchant(Enchantment.DAMAGE_ALL);
            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            stack.setItemMeta(itemMeta);
        }
    }

    public ItemStack getBukkitStack() {
        return stack;
    }

    public boolean canBreakBlocks() {
        return breakBlocks;
    }
}
