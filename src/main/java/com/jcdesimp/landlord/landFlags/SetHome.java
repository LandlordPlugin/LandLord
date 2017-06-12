package com.jcdesimp.landlord.landFlags;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.Landflag;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

/**
 * File created by jcdesimp on 4/16/14.
 */
public class SetHome extends Landflag {
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
    public SetHome(Landlord plugin) {
        super(plugin,
                plugin.getMessageConfig().getString("flags.setHome.displayName"),      //Display name (will be displayed to players)
                plugin.getMessageConfig().getString("flags.setHome.description"),
                new ItemStack(Material.BED),        //Itemstack (represented in manager)
                plugin.getMessageConfig().getString("flags.setHome.allowedTitle"),      //Text shown in manager for granted permission
                plugin.getMessageConfig().getString("flags.setHome.allowedText"),      //Description in manager for granted permission (ex: Friendly players <desc>)
                plugin.getMessageConfig().getString("flags.setHome.deniedTitle"),      //Text shown in manager for denied permission
                plugin.getMessageConfig().getString("flags.setHome.deniedText")       //Desciption in manager for denied permission (ex: Regular players <desc>)
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
    public void useSethome(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();

        if (p.isOp()) {
            return;
        }
        String cmd = event.getMessage();
        if (cmd != null) {
            if (cmd.contains("sethome")) {
                OwnedLand land = getPlugin().getLandManager().getApplicableLand(p.getLocation());
                if (land != null) {
                    if (!land.hasPermTo(p, this)) {
                        p.sendMessage(ChatColor.RED + getPlugin().getMessageConfig().getString("event.setHome"));
                        event.setCancelled(true);
                    }
                }

            }
        }
    }

}