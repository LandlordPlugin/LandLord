package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.UUIDFetcher;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Collections;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class UnfriendAll extends LandlordCommand {

    public void onUnfriendall(Player player, String name) {

        if (name == null || name.isEmpty()) {
            player.sendMessage(lm.getString("Commands.Addfriend.noPlayer")
                    .replace("%players%", Collections.singletonList("[]").toString()));
            return;
        }

        plugin.getPlayerManager().getOfflinePlayer(name, lPlayer -> {

            // Failure
            if (lPlayer == null) {
                player.sendMessage(lm.getString("Commands.UnfriendAll.noPlayer")
                        .replace("%players%", Collections.singletonList(name).toString()));
            } else {
                // Success
                int count = 0;
                for (ProtectedRegion protectedRegion : plugin.getWgHandler().getRegions(player.getUniqueId())) {
                    if (protectedRegion.getMembers().contains(lPlayer.getUuid())) {
                        protectedRegion.getMembers().removePlayer(lPlayer.getUuid());
                        count++;
                    }
                }

                if (count > 0) {
                    player.sendMessage(lm.getString("Commands.UnfriendAll.success")
                            .replace("%count%", String.valueOf(count))
                            .replace("%players%", name));
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            plugin.getMapManager().updateAll();
                        }
                    }.runTask(plugin);
                } else {
                    player.sendMessage(lm.getString("Commands.UnfriendAll.noFriend")
                            .replace("%player%", name));
                }
            }
        });
    }
}

