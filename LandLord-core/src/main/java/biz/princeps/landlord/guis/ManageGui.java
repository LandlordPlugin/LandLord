package biz.princeps.landlord.guis;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.ManageMode;
import biz.princeps.lib.gui.MultiPagedGUI;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

public class ManageGui extends AManage {

    public ManageGui(ILandLord plugin, Player player, IOwnedLand land) {
        super(plugin, player, plugin.getLangManager().getRawString("Commands.Manage.header")
                .replace("%info%", land.getName()), Lists.newArrayList(land), ManageMode.ONE, null, -1);
    }

    public ManageGui(ILandLord plugin, Player player, MultiPagedGUI landGui, IOwnedLand land) {
        super(plugin, player, landGui, plugin.getLangManager().getRawString("Commands.Manage.header")
                .replace("%info%", land.getName()), Lists.newArrayList(land), ManageMode.ONE, null, -1);
    }

}
