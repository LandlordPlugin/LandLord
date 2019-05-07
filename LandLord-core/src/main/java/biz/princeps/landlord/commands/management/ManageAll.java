package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ManageGUIAll;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 25/7/17
 * <p>
 * If you are looking for the gui, check AbstractManage
 */
public class ManageAll extends LandlordCommand {

    public void onManageAll(Player player) {
        ManageGUIAll gui = new ManageGUIAll(player, Lists.newArrayList(plugin.getWgproxy().getRegions(player.getUniqueId())));
        gui.display();
    }
}
