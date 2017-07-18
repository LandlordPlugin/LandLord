package biz.princeps.landlord.commands;

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
                                Iterator<UUID> iterator = defaultDomain.getUniqueIds().iterator();
                                while (iterator.hasNext()) {
                                    UUID uuid = iterator.next();
                                    if (!protectedRegion.getOwners().getUniqueIds().contains(uuid)) {
                                        protectedRegion.getMembers().removePlayer(uuid);
                                        i++;
                                    }
                                }
                            }
                        }
                        if (i > 0)
                            player.sendMessage(lm.getString("Commands.UnfriendAll.success")
                                    .replaceAll("%count%", String.valueOf(i))
                                    .replaceAll("%players%", Arrays.asList(names).toString()));
                        else
                            player.sendMessage(lm.getString("Commands.UnfriendAll.noFriend")
                                    .replaceAll("%player%", Arrays.asList(names).toString()));

                    }
                }.runTaskAsynchronously(plugin);

            }

            @Override
            public void onFailure(Throwable throwable) {
                player.sendMessage(lm.getString("Commands.UnfriendAll.noPlayer")
                        .replaceAll("%players%", Arrays.asList(names).toString()));
            }
        });


    }

}

