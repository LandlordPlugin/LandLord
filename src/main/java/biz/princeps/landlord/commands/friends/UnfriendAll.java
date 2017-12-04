package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.UUIDFetcher;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by spatium on 17.07.17.
 */
public class UnfriendAll extends LandlordCommand {

    public void onUnfriendall(Player player, String name) {

        UUIDFetcher.getUUID(name, uuid -> {

            // Failure
            if (uuid == null) {
                player.sendMessage(lm.getString("Commands.UnfriendAll.noPlayer")
                        .replace("%players%", Collections.singletonList(name).toString()));
            } else {
                // Success
                int i = 0;
                for (ProtectedRegion protectedRegion : plugin.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegions().values()) {
                    if (protectedRegion.isOwner(plugin.getWgHandler().getWG().wrapPlayer(player))) {

                        if (!protectedRegion.getOwners().getUniqueIds().contains(uuid)) {
                            protectedRegion.getMembers().removePlayer(uuid);
                            i++;
                        }

                    }
                }
                if (i > 0) {
                    player.sendMessage(lm.getString("Commands.UnfriendAll.success")
                            .replace("%count%", String.valueOf(i))
                            .replace("%players%", Arrays.asList(name).toString()));
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            plugin.getMapManager().updateAll();
                        }
                    }.runTask(plugin);
                } else {
                    player.sendMessage(lm.getString("Commands.UnfriendAll.noFriend")
                            .replace("%player%", Arrays.asList(name).toString()));
                }
            }
        });
    }
}

