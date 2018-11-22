package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.commands.LandlordCommand;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class AddfriendAll extends LandlordCommand {

    public void onAddfriend(Player player, String name) {
        if (name == null || name.isEmpty()) {
            lm.sendMessage(player, lm.getString("Commands.AddfriendAll.noPlayer")
                    .replace("%player%", name == null ? "[]" : name));
            return;
        }

        plugin.getPlayerManager().getOfflinePlayerAsync(name, lPlayer -> {
            if (lPlayer == null) {
                // Failure
                lm.sendMessage(player, lm.getString("Commands.AddfriendAll.noPlayer")
                        .replace("%player%", name));
            } else {
                int count = 0;
                for (ProtectedRegion protectedRegion : plugin.getWgHandler().getRegions(player.getUniqueId())) {
                    if (!protectedRegion.getMembers().getUniqueIds().contains(lPlayer.getUuid())) {
                        protectedRegion.getMembers().addPlayer(lPlayer.getUuid());
                        count++;
                    }
                }

                lm.sendMessage(player, lm.getString("Commands.AddfriendAll.success")
                        .replace("%player%", name)
                        .replace("%count%", count + ""));

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        plugin.getMapManager().updateAll();
                    }
                }.runTask(plugin);
            }
        });
    }
}

