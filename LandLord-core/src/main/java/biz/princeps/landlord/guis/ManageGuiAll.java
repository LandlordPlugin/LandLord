package biz.princeps.landlord.guis;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.ManageMode;
import biz.princeps.landlord.multi.MultiMode;
import biz.princeps.lib.gui.MultiPagedGUI;
import org.bukkit.entity.Player;

import java.util.List;

public class ManageGuiAll extends AManage {

    public ManageGuiAll(ILandLord plugin, Player player, List<IOwnedLand> land, ManageMode manageMode, MultiMode multiMode, int radius) {
        super(plugin, player, manageMode == ManageMode.ALL ? plugin.getLangManager().getRawString("Commands.Manage.all.header") :
                plugin.getLangManager().getRawString("Commands.Manage.multi.header"), land, manageMode, multiMode, radius);
    }

    public ManageGuiAll(ILandLord plugin, Player player, MultiPagedGUI landGui, List<IOwnedLand> land, ManageMode manageMode, MultiMode multiMode, int radius) {
        super(plugin, player, landGui, manageMode == ManageMode.ALL ? plugin.getLangManager().getRawString("Commands.Manage.all.header") :
                plugin.getLangManager().getRawString("Commands.Manage.multi.header"), land, manageMode, multiMode, radius);
    }

}
