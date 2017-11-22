package biz.princeps.landlord.items;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.util.CommandUtil;
import biz.princeps.lib.item.AbstractItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 11/22/17
 */
public class Maitem extends AbstractItem {
    // So called My-Item or Management-Item. Whatever sounds better to you <3

    private static final ItemStack STACK = new ItemStack(Material.valueOf(Landlord.getInstance().getConfig().getString("MaItem.item")));
    public static final String NAME = "maitem";

    public Maitem() {
        super(NAME, STACK, true);
    }

    @Override
    public void onClick(Action action, Player p, Location location) {
//TODO fix nullpointer in itemactionlistener after the server has restarted
        switch (action) {
            case LEFT_CLICK_BLOCK:
                break;
            case RIGHT_CLICK_BLOCK:
                // Display info
                CommandUtil.onInfo(location, p, Landlord.getInstance().getLangManager());
                break;
            case LEFT_CLICK_AIR:
                break;
            case RIGHT_CLICK_AIR:
                break;
            case PHYSICAL:
                break;
        }


    }


}
