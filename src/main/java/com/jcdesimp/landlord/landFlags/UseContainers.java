package com.jcdesimp.landlord.landFlags;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.LandManager;
import com.jcdesimp.landlord.landManagement.Landflag;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * File created by jcdesimp on 4/16/14.
 */
public class UseContainers extends Landflag {
 /*
  **********************
  * IMPORTANT!!!! Landlord will take care of registering
  * the listeners, all you need to do is register the
  * class with landlord's flagManager!!!
  **********************
  */

    /**
     * Constructor needs to be defined and properly call super()
     */
    public UseContainers(Landlord plugin) {
        super(plugin,
                plugin.getMessageConfig().getString("flags.useContainers.displayName"),      //Display name (will be displayed to players)
                plugin.getMessageConfig().getString("flags.useContainers.description"),
                new ItemStack(Material.CHEST),        //Itemstack (represented in manager)
                plugin.getMessageConfig().getString("flags.useContainers.allowedTitle"),      //Text shown in manager for granted permission
                plugin.getMessageConfig().getString("flags.useContainers.allowedText"),      //Description in manager for granted permission (ex: Friendly players <desc>)
                plugin.getMessageConfig().getString("flags.useContainers.deniedTitle"),      //Text shown in manager for denied permission
                plugin.getMessageConfig().getString("flags.useContainers.deniedText")       //Desciption in manager for denied permission (ex: Regular players <desc>)
        );
    }



    /*
     ******************************************************************************
     * ALL event handlers for this flag NEED to be defined inside this class!!!!!
     * REMEMBER! Do not register this class with bukkit, register with Landlord's
     * flag manager and landlord will register the event handlers.
     ******************************************************************************
     */


    /**
     * Event handler for block placements
     *
     * @param event that happened
     */


    /*
     *************************************
     * Of course u can register as many
     * event listeners as you need for
     * your flag to do it's job
     *************************************
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void useContainer(PlayerInteractEvent event) {

        String[] blockAccess = {"CHEST", "TRAPPED_CHEST", "BURNING_FURNACE", "FURNACE", "ANVIL", "DROPPER", "DISPENSER", "HOPPER", "BREWING_STAND", "SOIL", "BEACON", "JUKEBOX", "CAULDRON"};

        if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }

        if (event.getHand() == EquipmentSlot.OFF_HAND)
            return;

        if (!Arrays.asList(blockAccess).contains(event.getClickedBlock().getType().toString()) && !(event.getClickedBlock().getType().name().endsWith("SHULKER_BOX"))) {
            return;
        }
        OwnedLand land = LandManager.getApplicableLand(event.getClickedBlock().getLocation());
        if (land == null) {
            return;
        }
        Player p = event.getPlayer();
        if (!land.hasPermTo(p, this)) {
            p.sendMessage(ChatColor.RED + getPlugin().getMessageConfig().getString("event.useContainers.interact"));
            event.setCancelled(true);
        }
    }

}
