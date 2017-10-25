package biz.princeps.landlord.guis;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.gui.MultiPagedGUI;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 24.07.17.
 */
public class ManageGUIAll extends AbstractManage {

    public ManageGUIAll(Player player, OwnedLand... land) {
        super(player, Landlord.getInstance().getLangManager().getRawString("Commands.Manage.all.header"), land);
    }

    public ManageGUIAll(Player player, MultiPagedGUI landGui, OwnedLand... land) {
        super(player, landGui, Landlord.getInstance().getLangManager().getRawString("Commands.Manage.all.header"), land);
    }



}