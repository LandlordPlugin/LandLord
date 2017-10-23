package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.UUIDFetcher;
import com.google.common.util.concurrent.FutureCallback;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

public class Update extends LandlordCommand {


    public void onUpdateLands(World w) {
        plugin.getLogger().info("Starting to update lands...");


        for (ProtectedRegion pr : plugin.getWgHandler().getWG().getRegionManager(w).getRegions().values()) {

            if (pr.getId().split("_").length == 3 && Bukkit.getWorld(pr.getId().split("_")[0]) == w) {

                if (pr.getFlags().keySet().contains(DefaultFlag.USE)) {
                    pr.getFlags().remove(DefaultFlag.USE);
                }

                if(!pr.getFlags().keySet().contains(DefaultFlag.INTERACT)){
                    pr.setFlag(DefaultFlag.INTERACT, StateFlag.State.DENY);
                    pr.setFlag(DefaultFlag.INTERACT.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
                }

                if(!pr.getFlags().keySet().contains(DefaultFlag.CHEST_ACCESS)){
                    pr.setFlag(DefaultFlag.CHEST_ACCESS, StateFlag.State.DENY);
                    pr.setFlag(DefaultFlag.CHEST_ACCESS.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
                }

                if(!pr.getFlags().keySet().contains(DefaultFlag.CREEPER_EXPLOSION)){
                    pr.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
                }

                if(!pr.getFlags().keySet().contains(DefaultFlag.PVP)){
                    pr.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
                }

                if(!pr.getFlags().keySet().contains(DefaultFlag.BUILD)){
                    pr.setFlag(DefaultFlag.BUILD, StateFlag.State.DENY);
                    pr.setFlag(DefaultFlag.BUILD.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
                }



                String name = Bukkit.getOfflinePlayer(pr.getOwners().getUniqueIds().iterator().next()).getName();

                String greeting = lm.getRawString("Alerts.defaultGreeting").replace("%owner%", name);
                String farewell = lm.getRawString("Alerts.defaultFarewell").replace("%owner%", name);

                String actualGreeting = pr.getFlag(DefaultFlag.GREET_MESSAGE);
                String actualFarewell = pr.getFlag(DefaultFlag.FAREWELL_MESSAGE);

                if (!greeting.equals(actualGreeting)) {
                    pr.setFlag(DefaultFlag.GREET_MESSAGE, greeting);
                }
                if (!farewell.equals(actualFarewell)) {
                    pr.setFlag(DefaultFlag.FAREWELL_MESSAGE, farewell);
                }
            }

        }
        plugin.getLogger().info("Finished updating lands!");
    }
}