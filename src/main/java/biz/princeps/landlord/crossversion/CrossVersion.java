package biz.princeps.landlord.crossversion;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.crossversion.v1_11_R1.ActionBar;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 28.07.17.
 */
public class CrossVersion {

 //   static String version = Landlord.getInstance().getClass().getPackage().getName().substring(Landlord.getInstance().getClass().getPackage().getName().lastIndexOf('.') + 1);

    /**
     * just in case I ever want to upgrade this to a proper interface based system
     * @param player
     * @param msg
     */
    public static void sendActionBar(Player player, String msg) {
        new ActionBar().sendActionBar(player, msg);
    }
}
