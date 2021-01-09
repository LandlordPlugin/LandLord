package biz.princeps.lib.crossversion;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.inventory.ItemStack;

public class Item implements IItem {
    @Override
    public ItemStack addNBTTag(ItemStack stack, String key, Object value) {
        return NBTEditor.set(stack, value, key);
    }

    @Override
    public Object getValueFromNBT(ItemStack stack, String key) {
        return NBTEditor.getString(stack, key);
    }

    @Override
    public boolean hasNBTTag(ItemStack stack, String customItem) {
        return NBTEditor.getString(stack, customItem) != null;
    }
}
