package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.UUIDFetcher;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * Created by spatium on 17.07.17.
 */
public class AddfriendAll extends LandlordCommand {

    public void onAddfriend(Player player, String[] names) {

        if (this.worldDisabled(player)) {
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }

        for (int i = 0; i < names.length; i++) {
            String target = names[i];

            int finalI = i;
            UUIDFetcher.getUUID(target, uuid -> {

                if (uuid == null) {
                    // Failure
                    player.sendMessage(lm.getString("Commands.AddfriendAll.noPlayer")
                            .replace("%players%", Arrays.asList(names).toString()));
                } else {
                    // Success
                    int j = 0;
                    for (ProtectedRegion protectedRegion : plugin.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegions().values()) {
                        if (protectedRegion.isOwner(plugin.getWgHandler().getWG().wrapPlayer(player))) {
                            if (!protectedRegion.getOwners().getUniqueIds().contains(uuid)) {
                                protectedRegion.getMembers().addPlayer(uuid);
                                j++;
                            }
                        }
                    }
                    // Only execute on the last element
                    if (j > 0 && finalI == names.length - 1) {
                        player.sendMessage(lm.getString("Commands.AddfriendAll.success")
                                .replace("%count%", String.valueOf(j))
                                .replace("%players%", Arrays.asList(names).toString()));
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                plugin.getMapManager().updateAll();
                            }
                        }.runTask(plugin);
                    } else {
                        player.sendMessage(lm.getString("Commands.AddfriendAll.alreadyOwn"));

                    }
                }
            });
        }
    }
}

