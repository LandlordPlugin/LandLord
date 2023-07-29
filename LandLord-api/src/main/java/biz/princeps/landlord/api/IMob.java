package biz.princeps.landlord.api;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface IMob {

    /**
     * Gets the name of a mob (in uppercase)
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the corresponding head.
     *
     * @return the mob head
     */
    ItemStack getHead();

    /**
     * Gets the bukkit entity type.
     *
     * @return bukkit entity type
     */
    EntityType getType();

    /**
     * Gets the permission associated to this mob.
     *
     * @return a bukkit permission
     */
    String getPermission();

    /**
     * Gets a nicely formatted name of the mob.
     * Example: "Zombie Villager" instead of ZOMBIE_VILLAGER
     *
     * @return a nice name
     */
    String getNiceName();
}
