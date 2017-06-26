package com.jcdesimp.landlord.landFlags;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.Landflag;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;

/**
 * File created by jcdesimp on 4/16/14.
 */
/*
 *******************************************************
 * All flags need to extend the abstract Landflag class
 *******************************************************
 */
public class HarmAnimals extends Landflag {
 /*
  ************************************************************
  * IMPORTANT!!!! Landlord will take care of registering
  * the listeners, all you need to do is register the
  * class with landlord's flagManager!!!
  ************************************************************
  */

    public HashSet<EntityType> safeAnimals;

    /*
     * Constructor needs to be defined and properly call super()
     */
    public HarmAnimals(Landlord plugin) {
        super(plugin,
                plugin.getMessageConfig().getString("flags.harmAnimals.displayName"),      //Display name (will be displayed to players)
                plugin.getMessageConfig().getString("flags.harmAnimals.description"),
                new ItemStack(Material.LEATHER),        //Itemstack (represented in manager)
                plugin.getMessageConfig().getString("flags.harmAnimals.allowedTitle"),      //Text shown in manager for granted permission
                plugin.getMessageConfig().getString("flags.harmAnimals.allowedText"),      //Description in manager for granted permission (ex: Friendly players <desc>)
                plugin.getMessageConfig().getString("flags.harmAnimals.deniedTitle"),      //Text shown in manager for denied permission
                plugin.getMessageConfig().getString("flags.harmAnimals.deniedText")       //Desciption in manager for denied permission (ex: Regular players <desc>)
        );
        safeAnimals = new HashSet<>(Arrays.asList(
                EntityType.OCELOT,
                EntityType.WOLF,
                EntityType.HORSE,
                EntityType.COW,
                EntityType.PIG,
                EntityType.MUSHROOM_COW,
                EntityType.SHEEP,
                EntityType.CHICKEN,
                EntityType.RABBIT,
                EntityType.LLAMA,
                EntityType.DONKEY,
                EntityType.MULE,
                EntityType.POLAR_BEAR,
                EntityType.SKELETON_HORSE,
                EntityType.ZOMBIE_HORSE,
                EntityType.SNOWMAN,
                EntityType.IRON_GOLEM,
                EntityType.VILLAGER,
                EntityType.SQUID,
                EntityType.PARROT
        ));
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
    @EventHandler(priority = EventPriority.HIGH)
    public void animalDamage(EntityDamageByEntityEvent event) {
        org.bukkit.entity.Entity victim = event.getEntity();
        if (!safeAnimals.contains(victim.getType())) {
            System.out.println("not a protected animal");
            return;
        }

        org.bukkit.entity.Entity attacker = event.getDamager();

        if (attacker.getType().toString().equals("PLAYER")) {
            Player p = (Player) attacker;
            OwnedLand land = getPlugin().getLandManager().getApplicableLand(victim.getLocation());
            if (land == null) {
                return;
            }
            if (!land.hasPermTo(p, this)) {

                p.sendMessage(ChatColor.RED + getPlugin().getMessageConfig().getString("event.harmAnimals.melee"));

                event.setCancelled(true);

            }

        } else if (attacker.getType().toString().equalsIgnoreCase("Arrow") || attacker.getType().toString().equalsIgnoreCase("SPLASH_POTION")) {
            Projectile a = (Projectile) attacker;
            Player p;
            if (a.getShooter() instanceof Player) {
                OwnedLand land = getPlugin().getLandManager().getApplicableLand(victim.getLocation());
                p = (Player) a.getShooter();
                if (land == null) {
                    return;
                }
                //System.out.println(a.getType());
                if (!land.hasPermTo(p, this)) {
                    if (a.getType().toString().equals("ARROW")) {
                        p.sendMessage(ChatColor.RED + getPlugin().getMessageConfig().getString("event.harmAnimals.projectile"));
                    }
                    a.remove();
                    event.setCancelled(true);
                }
            }


        }
    }


}
