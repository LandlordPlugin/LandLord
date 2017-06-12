package com.jcdesimp.landlord.landFlags;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.Landflag;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * File created by jcdesimp on 4/16/14.
 */
public class UseRedstone extends Landflag {
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
    public UseRedstone(Landlord plugin) {
        super(plugin,
                plugin.getMessageConfig().getString("flags.useRedstone.displayName"),      //Display name (will be displayed to players)
                plugin.getMessageConfig().getString("flags.useRedstone.description"),
                new ItemStack(Material.REDSTONE),        //Itemstack (represented in manager)
                plugin.getMessageConfig().getString("flags.useRedstone.allowedTitle"),      //Text shown in manager for granted permission
                plugin.getMessageConfig().getString("flags.useRedstone.allowedText"),      //Description in manager for granted permission (ex: Friendly players <desc>)
                plugin.getMessageConfig().getString("flags.useRedstone.deniedTitle"),      //Text shown in manager for denied permission
                plugin.getMessageConfig().getString("flags.useRedstone.deniedText")       //Desciption in manager for denied permission (ex: Regular players <desc>)
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
    public void useRedstone(PlayerInteractEvent event) {
        Material[] blockedInteracts = {Material.WOOD_BUTTON, Material.STONE_BUTTON, Material.LEVER, Material.GOLD_PLATE,
                Material.IRON_PLATE, Material.STONE_PLATE, Material.WOOD_PLATE, Material.TRIPWIRE};
        //System.out.println("Type Clicked: "+event.getClickedBlock().getType());
        //System.out.println("Action: "+event.getAction());
        //System.out.println("Item Used: "+event.getItem());
        if (event.getPlayer() == null) {
            //System.out.println("OUTT");
            return;
        }
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && Arrays.asList(blockedInteracts).contains(event.getClickedBlock().getType())) {
            //System.out.println("left click");
            OwnedLand land = getPlugin().getLandManager().getApplicableLand(event.getClickedBlock().getLocation());
            if (land == null) {
                return;
            }
            if (!land.hasPermTo(p, this)) {
                p.sendMessage(ChatColor.RED + getPlugin().getMessageConfig().getString("event.useRedstone.interact"));
                event.setCancelled(true);
                return;
            }
        }
        if (event.getAction().equals(Action.PHYSICAL) && Arrays.asList(blockedInteracts).contains(event.getClickedBlock().getType())) {
            //System.out.println("physical");
            OwnedLand land = getPlugin().getLandManager().getApplicableLand(event.getClickedBlock().getLocation());
            if (land == null) {
                return;
            }
            if (!land.hasPermTo(p, this)) {
                event.setCancelled(true);
            }
        }

    }

}