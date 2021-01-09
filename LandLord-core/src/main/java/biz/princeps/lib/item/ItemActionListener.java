package biz.princeps.lib.item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Project: PrincepsLib
 * Author: Alex D. (SpatiumPrinceps)
 * Date: 11/7/17 11:42 AM
 */
public class ItemActionListener implements Listener {

    private final ItemManager itemManager;

    public ItemActionListener(ItemManager manager) {
        //PrincepsLib.getPluginInstance().getServer().getPluginManager().registerEvents(this, PrincepsLib.getPluginInstance());
        this.itemManager = manager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
/*TODO reimplement

        if (event.getHand() == EquipmentSlot.OFF_HAND){
            return;
        }

        ItemStack item = event.getItem();
        if (item != null) {
            if (AbstractItem.isCustomItem(item)) {
                AbstractItem abstractItem = itemManager.getAbstractItem(item);
                if (abstractItem != null)
                    abstractItem.onClick(event.getAction(), event.getPlayer(), (event.getClickedBlock() == null ? null : event.getClickedBlock().getLocation()));
                else
                    PrincepsLib.getPluginInstance().getLogger().warning("Invalid custom item found!!!");
            }
        }

 */
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        //TODO replace with new method. Dunno and to lazy to google it
        /* TODO reimplement
        ItemStack item = e.getPlayer().getItemInHand();
        if (item != null) {
            if (AbstractItem.isCustomItem(item)) {
                AbstractItem abstractItem = itemManager.getAbstractItem(item);
                if (abstractItem != null) {
                    if (!abstractItem.canBreakBlocks()) {
                        e.setCancelled(true);
                    }
                } else {
                    PrincepsLib.getPluginInstance().getLogger().warning("Invalid custom item found!!!");
                }
            }
        }

         */


    }
}
