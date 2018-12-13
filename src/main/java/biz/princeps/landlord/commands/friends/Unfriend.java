package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.OwnedLand;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class Unfriend extends LandlordCommand {

    public void onUnfriend(Player player, String[] names) {

        if (this.worldDisabled(player)) {
            lm.sendMessage(player, lm.getString("Disabled-World"));
            return;
        }
        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

        OwnedLand land = plugin.getWgHandler().getRegion(chunk);
        if (land != null) {
            if (!land.isOwner(player.getUniqueId()) && !player.hasPermission("landlord.admin.modifyfriends")) {
                lm.sendMessage(player, lm.getString("Commands.Unfriend.notOwn")
                        .replace("%owner%", land.printOwners()));
                return;
            }

            for (String target : names) {
                plugin.getPlayerManager().getOfflinePlayerAsync(target, lPlayer -> {
                    if (lPlayer == null) {
                        // Failure
                        lm.sendMessage(player, lm.getString("Commands.Unfriend.noPlayer")
                                .replace("%players%", Arrays.asList(names).toString()));
                    } else {
                        // Success
                        land.removeFriend(lPlayer.getUuid());
                        LandManageEvent landManageEvent = new LandManageEvent(player, land,
                                null, "FRIENDS", land.printMembers());
                        Bukkit.getPluginManager().callEvent(landManageEvent);

                        lm.sendMessage(player, lm.getString("Commands.Unfriend.success")
                                .replace("%players%", Arrays.asList(names).toString()));
                    }
                });
            }

            new BukkitRunnable() {

                @Override
                public void run() {
                    plugin.getMapManager().updateAll();
                }
            }.runTaskLater(plugin, 60L);
        }
    }
}

