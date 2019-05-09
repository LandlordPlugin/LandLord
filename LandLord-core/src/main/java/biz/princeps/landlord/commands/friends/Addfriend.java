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
public class Addfriend extends LandlordCommand {

    public Addfriend(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Addfriend.name"),
                pl.getConfig().getString("CommandSettings.Addfriend.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Addfriend.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Addfriend.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        Player player = properties.getPlayer();
        String[] names = arguments.get();

        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

        IOwnedLand land = plugin.getWGProxy().getRegion(chunk);
        if (land != null) {
            if (!land.isOwner(player.getUniqueId()) && !player.hasPermission("landlord.admin.modifyfriends")) {
                lm.sendMessage(player, lm.getString("Commands.Addfriend.notOwn")
                        .replace("%owner%", land.getOwnersString()));
                return;
            }

            if (names.length == 0) {
                lm.sendMessage(player, lm.getString("Commands.Addfriend.noPlayer")
                        .replace("%players%", Arrays.asList(names).toString()));
                return;
            }

            for (String target : names) {

                plugin.getPlayerManager().getOfflinePlayerAsync(target, lPlayer -> {
                    if (lPlayer == null) {
                        // TODO fix inconsistency with printing the entire names list
                        lm.sendMessage(player, lm.getString("Commands.Addfriend.noPlayer")
                                .replace("%players%", Arrays.asList(names).toString()));
                    } else {
                        // Success
                        if (!land.isOwner(lPlayer.getUuid())) {
                            land.addFriend(lPlayer.getUuid());
                            LandManageEvent landManageEvent = new LandManageEvent(player, land,
                                    null, "FRIENDS", land.getMembersString());
                            Bukkit.getPluginManager().callEvent(landManageEvent);

                            lm.sendMessage(player, lm.getString("Commands.Addfriend.success")
                                    .replace("%players%", Arrays.asList(names).toString()));
                        } else {
                            lm.sendMessage(player, lm.getString("Commands.Addfriend.alreadyOwn"));
                        }
                    }
                });

                // lets delay it, because we cant be sure, that the requests are done when executing this piece of code
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getMapManager().updateAll();
                    }
                }.runTaskLater(plugin.getPlugin(), 60L);
            }
        }
    }
}