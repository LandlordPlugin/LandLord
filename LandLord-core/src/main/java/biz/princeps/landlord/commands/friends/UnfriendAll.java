package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class UnfriendAll extends LandlordCommand {

    public void onUnfriendall(Player player, String name) {

        if (name == null || name.isEmpty()) {
            lm.sendMessage(player, lm.getString("Commands.Addfriend.noPlayer")
                    .replace("%players%", Collections.singletonList("[]").toString()));
            return;
        }

        plugin.getPlayerManager().getOfflinePlayerAsync(name, lPlayer -> {

            // Failure
            if (lPlayer == null) {
                lm.sendMessage(player, lm.getString("Commands.UnfriendAll.noPlayer")
                        .replace("%players%", Collections.singletonList(name).toString()));
            } else {
                // Success
                int count = 0;
                for (IOwnedLand ol : plugin.getWgproxy().getRegions(player.getUniqueId())) {
                    if (ol.isFriend(lPlayer.getUuid())) {
                        ol.removeFriend(lPlayer.getUuid());
                        count++;
                        LandManageEvent landManageEvent = new LandManageEvent(player, ol,
                                null, "FRIENDS", ol.getMembersString());
                        Bukkit.getPluginManager().callEvent(landManageEvent);
                    }
                }

                if (count > 0) {
                    lm.sendMessage(player, lm.getString("Commands.UnfriendAll.success")
                            .replace("%count%", String.valueOf(count))
                            .replace("%players%", name));
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            plugin.getMapManager().updateAll();
                        }
                    }.runTask(plugin.getPluginInstance());
                } else {
                    lm.sendMessage(player, lm.getString("Commands.UnfriendAll.noFriend")
                            .replace("%player%", name));
                }
            }
        });
    }
}

