package biz.princeps.landlord.commands;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.manager.LangManager;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 16/7/17
 * <p>
 * Every landlord command should extend this class.
 * Actually, this is not required, but advised, since you dont have to get the instances for pluging and LangManager yourself.
 * In addition, there's a disabled world check, which comes in handy.
 * <p>
 * Major command logic is done in Landlordbase
 */
public abstract class LandlordCommand {

    protected Landlord plugin = Landlord.getInstance();
    protected LangManager lm = plugin.getLangManager();

    public boolean worldDisabled(Player player) {
        return plugin.getConfig().getStringList("disabled-worlds").contains(player.getWorld().getName());
    }


}
