package biz.princeps.landlord.guis;

import biz.princeps.landlord.api.ILandLord;
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

    public ManageGUIAll(ILandLord pl, Player player, List<IOwnedLand> land) {
        super(pl, player, pl.getLangManager().getRawString("Commands.Manage.all.header"), land);
    }

    public ManageGUIAll(ILandLord pl, Player player, MultiPagedGUI landGui, List<IOwnedLand> land) {
        super(pl, player, landGui, pl.getLangManager().getRawString("Commands.Manage.all.header"), land);
    }


}