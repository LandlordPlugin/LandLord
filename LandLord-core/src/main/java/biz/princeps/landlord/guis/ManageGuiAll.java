package biz.princeps.landlord.guis;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.lib.gui.MultiPagedGUI;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

import java.util.List;

public class ManageGuiAll extends AManage {

    public ManageGuiAll(ILandLord pl, Player player, List<IOwnedLand> land) {
        super(pl, player, pl.getLangManager().getRawString("Commands.Manage.all.header"), land);
    }

    public ManageGuiAll(ILandLord pl, Player player, MultiPagedGUI landGui, List<IOwnedLand> land) {
        super(pl, player, landGui, pl.getLangManager().getRawString("Commands.Manage.all.header"), land);
    }
}
