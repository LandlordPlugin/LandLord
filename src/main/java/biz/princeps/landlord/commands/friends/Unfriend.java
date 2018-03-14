package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.landlord.util.UUIDFetcher;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * Created by spatium on 17.07.17.
 */
public class Unfriend extends LandlordCommand {

    public void onUnfriend(Player player, String[] names) {

        if (this.worldDisabled(player)) {
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }
        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

        OwnedLand land = plugin.getWgHandler().getRegion(chunk);
        if (land != null) {
            if (!land.isOwner(player.getUniqueId()) && !player.hasPermission("landlord.admin.modifyfriends")) {
                player.sendMessage(lm.getString("Commands.Unfriend.notOwn")
                        .replace("%owner%", land.printOwners()));
                return;
            }

            for (String target : names) {
                UUIDFetcher.getUUID(target, uuid -> {

                    if (uuid == null) {
                        // Failure
                        player.sendMessage(lm.getString("Commands.Unfriend.noPlayer")
                                .replace("%players%", Arrays.asList(names).toString()));
                    } else {
                        // Success
                        land.removeFriend(uuid);
                    }

                });
            }

            player.sendMessage(lm.getString("Commands.Unfriend.success")
                    .replace("%players%", Arrays.asList(names).toString()));

            new BukkitRunnable() {

                @Override
                public void run() {
                    plugin.getMapManager().updateAll();
                }
            }.runTaskLater(plugin, 60L);
        }
    }
}

