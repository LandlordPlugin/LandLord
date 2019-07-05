package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
        if (arguments.size() == 0) {
            lm.sendMessage(player, lm.getString("Commands.Addfriend.noPlayer")
                    .replace("%players%", "?"));
            return;
        }

        try {
            IOwnedLand targetLand;
            String targetPlayer;
            if (arguments.size() == 1) {
                targetPlayer = arguments.get(0);
                targetLand = plugin.getWGManager().getRegion(player.getLocation().getChunk());
            } else {
                targetPlayer = arguments.get(0);
                targetLand = plugin.getWGManager().getRegion(arguments.get(1));
            }
            onUnfriend(player, targetPlayer, targetLand);
        } catch (ArgumentsOutOfBoundsException e) {
            properties.sendUsage();
        }
    }

    private void onUnfriend(Player player, String playerName, IOwnedLand land) {

        if (isDisabledWorld(player)) {
            return;
        }

        if (land != null) {
            if (!land.isOwner(player.getUniqueId()) && !player.hasPermission("landlord.admin.modifyfriends")) {
                lm.sendMessage(player, lm.getString("Commands.Unfriend.notOwn")
                        .replace("%owner%", land.getOwnersString()));
                return;
            }

            plugin.getPlayerManager().getOffline(playerName, (offline) -> {
                if (offline == null) {
                    // Failure
                    lm.sendMessage(player, lm.getString("Commands.Unfriend.noPlayer")
                            .replace("%players%", playerName));
                } else if (land.getFriends().contains(offline.getUuid())) {
                    // Success
                    String old = land.getMembersString();
                    land.removeFriend(offline.getUuid());

                    Bukkit.getScheduler().runTask(plugin.getPlugin(),()->{
                        LandManageEvent landManageEvent = new LandManageEvent(player, land,
                                "FRIENDS", old, land.getMembersString());
                        Bukkit.getPluginManager().callEvent(landManageEvent);
                    });

                    lm.sendMessage(player, lm.getString("Commands.Unfriend.success")
                            .replace("%players%", playerName));

                    // lets delay it, because we cant be sure, that the requests are done when executing this piece
                    // of code
                    Bukkit.getScheduler().runTaskLater(plugin.getPlugin(), plugin.getMapManager()::updateAll, 60L);
                } else {
                    lm.sendMessage(player, lm.getString("Commands.UnfriendAll.noFriend")
                            .replace("%player%", playerName));
                }
            });
        }
    }
}

