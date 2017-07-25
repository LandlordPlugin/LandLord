package biz.princeps.landlord.commands;

import biz.princeps.landlord.guis.ManageGUIAll;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 25.07.17.
 */
public class ManageAll extends LandlordCommand{


    public void onManageAll(Player player) {


        ManageGUIAll gui = new ManageGUIAll(player);
        gui.display();
    }
}
