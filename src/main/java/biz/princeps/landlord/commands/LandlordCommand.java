package biz.princeps.landlord.commands;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.manager.LangManager;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 16/7/17
 */
public abstract class LandlordCommand {

    protected Landlord plugin = Landlord.getInstance();
    protected LangManager lm = plugin.getLangManager();

    public boolean worldDisabled(Player player){
        return plugin.getConfig().getStringList("disabled-worlds").contains(player.getWorld().getName());
    }


}
