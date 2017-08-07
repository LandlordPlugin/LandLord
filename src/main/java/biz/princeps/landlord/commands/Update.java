package biz.princeps.landlord.commands;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class Update extends LandlordCommand {


    public void onUpdateLands(World w) {

        plugin.getWgHandler().getWG().getRegionManager(w).getRegions().values().forEach(pr -> {

            if (!pr.getFlags().keySet().contains(DefaultFlag.USE)) {
                pr.setFlag(DefaultFlag.USE, StateFlag.State.DENY);
                pr.setFlag(DefaultFlag.USE.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
            }

            String greeting = lm.getRawString("Alerts.defaultGreeting").replace("%owner%", Bukkit.getOfflinePlayer(pr.getOwners().getUniqueIds().iterator().next()).getName());
            String farewell = lm.getRawString("Alerts.defaultFarewell").replace("%owner%", Bukkit.getOfflinePlayer(pr.getOwners().getUniqueIds().iterator().next()).getName());

            String actualGreetign = pr.getFlag(DefaultFlag.GREET_MESSAGE);
            String actualFarewell = pr.getFlag(DefaultFlag.FAREWELL_MESSAGE);

            if(!greeting.equals(actualGreetign)){
                pr.setFlag(DefaultFlag.GREET_MESSAGE, greeting);
            }
            if(!farewell.equals(actualFarewell)){
                pr.setFlag(DefaultFlag.FAREWELL_MESSAGE, farewell);
            }


        });

    }

}
