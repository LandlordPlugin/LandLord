package biz.princeps.lib.gui.simple;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spatium on 21.07.17.
 */
public class Icon {

    public final List<Action> clickActions = new ArrayList<>();
    public ItemStack itemStack;

    public Icon(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Icon addClickAction(Action clickAction) {
        this.clickActions.add(clickAction);
        return this;
    }

    public List<Action> getClickActions() {
        return this.clickActions;
    }

    public Icon setName(String name) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return this;
    }

    public List<String> getLore() {
        return itemStack.getItemMeta().getLore();
    }

    public Icon setLore(List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return this;
    }
}
