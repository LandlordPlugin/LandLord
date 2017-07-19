package biz.princeps.landlord.commands;

import biz.princeps.landlord.util.UUIDFetcher;
import com.google.common.util.concurrent.FutureCallback;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by spatium on 19.07.17.
 */
public class Clear extends LandlordCommand {

    public void onClearWorld(Player player, String target) {
        if (this.worldDisabled(player)) {
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                World world = player.getWorld();
                RegionManager regionManager = plugin.getWgHandler().getWG().getRegionManager(world);
                Map<String, ProtectedRegion> regions = regionManager.getRegions();
                int count;

                if (target == null) {
                    count = regions.size();
                    regions.keySet().forEach(regionManager::removeRegion);

                    player.sendMessage(lm.getString("Commands.ClearWorld.success")
                            .replaceAll("%count%", String.valueOf(count))
                            .replaceAll("%world%", world.getName()));
                } else {

                    UUIDFetcher.getInstance().namesToUUID(new String[]{target}, new FutureCallback<DefaultDomain>() {
                        @Override
                        public void onSuccess(@Nullable DefaultDomain domain) {
                            Set<String> todelete = new HashSet<>();
                            UUID id = domain.getUniqueIds().iterator().next();
                            System.out.println(id);
                            regions.values().stream().filter(pr -> pr.getOwners().getUniqueIds().contains(id)).forEach(pr -> todelete.add(pr.getId()));

                            int count = todelete.size();

                            todelete.forEach(regionManager::removeRegion);
                            player.sendMessage(lm.getString("Commands.ClearWorld.successPlayer")
                                    .replaceAll("%count%", String.valueOf(count))
                                    .replaceAll("%player%", target));
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            player.sendMessage(lm.getString("Commands.ClearWorld.noPlayer")
                                    .replaceAll("%players%", Arrays.asList(target).toString()));
                        }
                    });


                }
                plugin.getMapManager().updateAll();
            }
        }.runTaskAsynchronously(plugin);
    }
}
