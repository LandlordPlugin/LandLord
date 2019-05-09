package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
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

    public Unfriend(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.RemoveFriend.name"),
                pl.getConfig().getString("CommandSettings.RemoveFriend.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.RemoveFriend.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.RemoveFriend.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        Player player = properties.getPlayer();
        String[] names = arguments.get();

        if (isDisabledWorld(player)) return;

        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

        IOwnedLand land = plugin.getWGProxy().getRegion(chunk);
        if (land != null) {
            if (!land.isOwner(player.getUniqueId()) && !player.hasPermission("landlord.admin.modifyfriends")) {
                lm.sendMessage(player, lm.getString("Commands.Unfriend.notOwn")
                        .replace("%owner%", land.getOwnersString()));
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
                                null, "FRIENDS", land.getMembersString());
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
            }.runTaskLater(plugin.getPlugin(), 60L);
        }
    }
}

