package biz.princeps.landlord.guis;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.lib.gui.MultiPagedGUI;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 24/7/17
 */
public class ManageGUIAll extends AbstractManage {

    public ManageGUIAll(Player player, List<IOwnedLand> land) {
        super(player, Landlord.getInstance().getLangManager().getRawString("Commands.Manage.all.header"), land);
    }

    public ManageGUIAll(Player player, MultiPagedGUI landGui, List<IOwnedLand> land) {
        super(player, landGui, Landlord.getInstance().getLangManager().getRawString("Commands.Manage.all.header"), land);
    }


}