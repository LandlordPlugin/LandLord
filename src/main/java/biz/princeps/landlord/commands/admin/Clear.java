package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.UUIDFetcher;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 19/07/17
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
                // Clearing all regions in one world
                if (target == null) {
                    World world = player.getWorld();
                    RegionManager regionManager = plugin.getWgHandler().getWG().getRegionManager(world);

                    Map<String, ProtectedRegion> regions = regionManager.getRegions();
                    int count;

                    count = regions.size();
                    regions.keySet().forEach(regionManager::removeRegion);

                    player.sendMessage(lm.getString("Commands.ClearWorld.success")
                            .replace("%count%", String.valueOf(count))
                            .replace("%world%", world.getName()));

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.getMapManager().updateAll());
                } else {
                    // Clear only a specific player
                    plugin.getPlayerManager().getOfflinePlayerAsync(target, lPlayer -> {

                        if (lPlayer == null) {
                            // Failure
                            player.sendMessage(lm.getString("Commands.ClearWorld.noPlayer")
                                    .replace("%players%", target));
                        } else {
                            // Success
                            int amt = 0;
                            for (World world : Bukkit.getWorlds()) {
                                // Only count enabled worlds
                                if (!Landlord.getInstance().getConfig().getStringList("disabled-worlds").contains(world.getName())) {
                                    List<ProtectedRegion> rgs = plugin.getWgHandler().getRegions(lPlayer.getUuid(), world);
                                    amt += rgs.size();
                                    Set<String> toDelete = new HashSet<>();
                                    for (ProtectedRegion protectedRegion : rgs) {
                                        toDelete.add(protectedRegion.getId());
                                    }
                                    RegionManager rgm = plugin.getWgHandler().getWG().getRegionManager(world);
                                    for (String s : toDelete) {
                                        plugin.getOfferManager().removeOffer(s);
                                        rgm.removeRegion(s);
                                    }
                                }
                            }

                            player.sendMessage(lm.getString("Commands.ClearWorld.successPlayer")
                                    .replace("%count%", String.valueOf(amt))
                                    .replace("%player%", target));

                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.getMapManager().updateAll());
                        }
                    });
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
