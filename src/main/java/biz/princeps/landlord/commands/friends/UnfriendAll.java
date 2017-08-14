package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.UUIDFetcher;
import com.google.common.util.concurrent.FutureCallback;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by spatium on 17.07.17.
 */
public class UnfriendAll extends LandlordCommand {

    public void onUnfriendall(Player player, String[] names) {

        UUIDFetcher.getInstance().namesToUUID(names, new FutureCallback<DefaultDomain>() {
            @Override
            public void onSuccess(@Nullable DefaultDomain defaultDomain) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        int i = 0;
                        for (ProtectedRegion protectedRegion : plugin.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegions().values()) {
                            if (protectedRegion.isOwner(plugin.getWgHandler().getWG().wrapPlayer(player))) {
                                for (UUID uuid : defaultDomain.getUniqueIds()) {
                                    if (!protectedRegion.getOwners().getUniqueIds().contains(uuid)) {
                                        protectedRegion.getMembers().removePlayer(uuid);
                                        i++;
                                    }
                                }
                            }
                        }
                        if (i > 0) {
                            player.sendMessage(lm.getString("Commands.UnfriendAll.success")
                                    .replace("%count%", String.valueOf(i))
                                    .replace("%players%", Arrays.asList(names).toString()));
                            plugin.getMapManager().updateAll();
                        } else
                            player.sendMessage(lm.getString("Commands.UnfriendAll.noFriend")
                                    .replace("%player%", Arrays.asList(names).toString()));

                    }
                }.runTaskAsynchronously(plugin);

            }

            @Override
            public void onFailure(Throwable throwable) {
                player.sendMessage(lm.getString("Commands.UnfriendAll.noPlayer")
                        .replace("%players%", Arrays.asList(names).toString()));
            }
        });


    }

}

