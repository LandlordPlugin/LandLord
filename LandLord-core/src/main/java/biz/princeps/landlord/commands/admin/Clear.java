package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ClearGUI;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 19/07/17
 */
public class Clear extends LandlordCommand {

    public Clear(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Clear.name"),
                pl.getConfig().getString("CommandSettings.Clear.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Clear.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Clear.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        Player player = properties.getPlayer();

        if (isDisabledWorld(player)) return;

        /*
         * Clear Options:
         * 1. Clear all for player x        (target==x || player stands inside x claim)
         * 2. Clear only specific claim     (target==null)
         * 3. Clear entire world            (target==null)
         */
        ClearGUI clearGUI = new ClearGUI(plugin, player);
        clearGUI.display();
    }
}
