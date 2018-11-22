package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ManageGUIAll;
import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 25/7/17
 */
public class ManageAll extends LandlordCommand {


    public void onManageAll(Player player) {

        List<OwnedLand> lands = new ArrayList<>();

        for (World world : Bukkit.getWorlds()) {
            for (ProtectedRegion pr : plugin.getWgHandler().getRegions(player.getUniqueId(), world)) {
                lands.add(plugin.getLand(pr));
            }
        }

        ManageGUIAll gui = new ManageGUIAll(player, lands);
        gui.display();
    }
}
