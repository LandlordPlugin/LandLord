package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ClearGUI;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 19/07/17
 */
public class Clear extends LandlordCommand {

    public Clear(ILandLord plugin) {
        super(plugin);
    }

    public void onClearWorld(Player player) {
        if (this.worldDisabled(player)) {
            lm.sendMessage(player, lm.getString("Disabled-World"));
            return;
        }
        /*
         * Clear Options:
         * 1. Clear all for player x        (target==x || player stands inside x claim)
         * 2. Clear only specific claim     (target==null)
         * 3. Clear entire world            (target==null)
         */
        ClearGUI clearGUI = new ClearGUI(player);
        clearGUI.display();
    }
}
