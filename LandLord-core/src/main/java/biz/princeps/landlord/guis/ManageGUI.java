package biz.princeps.landlord.guis;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.lib.gui.MultiPagedGUI;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class ManageGUI extends AbstractManage {
    public ManageGUI(Player player, IOwnedLand land) {
        super(player, Landlord.getInstance().getLangManager().getRawString("Commands.Manage.header")
                .replace("%info%", land.getName()), Lists.newArrayList(land));
    }

    public ManageGUI(Player player, MultiPagedGUI landGui, IOwnedLand land) {
        super(player, landGui, Landlord.getInstance().getLangManager().getRawString("Commands.Manage.header")
                .replace("%info%", land.getName()), Lists.newArrayList(land));
    }
}
