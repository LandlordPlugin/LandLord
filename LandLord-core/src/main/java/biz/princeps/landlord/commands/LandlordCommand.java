package biz.princeps.landlord.commands;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.lib.command.SubCommand;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Set;

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
public abstract class LandlordCommand extends SubCommand {

    protected ILandLord plugin;
    protected ILangManager lm;

    public LandlordCommand(ILandLord plugin, String name, String usage, Set<String> permissions, Set<String> aliases) {
        super(name, usage, permissions, aliases);
        this.plugin = plugin;
        this.lm = plugin.getLangManager();
    }

    public boolean isDisabledWorld(Player player) {
        if (plugin.getConfig().getStringList("disabled-worlds").contains(player.getWorld().getName())) {
            lm.sendMessage(player, lm.getString("Disabled-World"));
            return true;
        }
        return false;
    }

    public boolean isDisabledWorld(Player player, World world) {
        if (plugin.getConfig().getStringList("disabled-worlds").contains(world.getName())) {
            lm.sendMessage(player, lm.getString("Disabled-World"));
            return true;
        }
        return false;
    }


}
