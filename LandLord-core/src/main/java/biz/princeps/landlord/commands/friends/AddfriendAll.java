package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class AddfriendAll extends LandlordCommand {

    public AddfriendAll(ILandLord plugin) {
        super(plugin);
    }

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
                for (IOwnedLand ol : plugin.getWGProxy().getRegions(player.getUniqueId())) {
                    if (!ol.isFriend(lPlayer.getUuid())) {
                        ol.addFriend(lPlayer.getUuid());
                        count++;
                        LandManageEvent landManageEvent = new LandManageEvent(player, ol,
                                null, "FRIENDS", ol.getMembersString());
                        Bukkit.getPluginManager().callEvent(landManageEvent);
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
                }.runTask(plugin.getPlugin());
            }
        });
    }
}

