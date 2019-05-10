package biz.princeps.landlord.api;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface IMob {

    String getName();

    ItemStack getEgg();

    EntityType getType();

    String getNiceName();
}
