package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.landlord.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * Created by spatium on 17.07.17.
 */
public class Addfriend extends LandlordCommand {

    public void onAddfriend(Player player, String[] names) {

        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

        OwnedLand land = plugin.getWgHandler().getRegion(chunk);
        if (land != null) {
            if (!land.isOwner(player.getUniqueId()) && !player.hasPermission("landlord.admin.modifyfriends")) {
                player.sendMessage(lm.getString("Commands.Addfriend.notOwn")
                        .replace("%owner%", land.printOwners()));
                return;
            }

            for (String target : names) {
                UUIDFetcher.getUUID(target, uuid -> {

                    if (uuid == null) {
                        // TODO FIX THIS SHIT
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "region addmember " + land.getName() + " " + names[0]);

                        // Failure
                        // player.sendMessage(lm.getString("Commands.Addfriend.noPlayer")
                        //  .replace("%players%", Arrays.asList(names).toString()));
                    } else {
                        // Success
                        if (!land.getWGLand().getOwners().getUniqueIds().contains(uuid)) {
                            land.getWGLand().getMembers().addPlayer(uuid);
                            player.sendMessage(lm.getString("Commands.Addfriend.success")
                                    .replace("%players%", Arrays.asList(names).toString()));
                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    plugin.getMapManager().updateAll();
                                }
                            }.runTask(plugin);

                        } else {
                            player.sendMessage(lm.getString("Commands.Addfriend.alreadyOwn"));
                        }
                    }

                });
            }
        }
    }
}