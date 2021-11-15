package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class Unfriend extends LandlordCommand {

    public Unfriend(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.RemoveFriend.name"),
                plugin.getConfig().getString("CommandSettings.RemoveFriend.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.RemoveFriend.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.RemoveFriend.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }
        Player player = properties.getPlayer();
        if (arguments.size() == 0) {
            lm.sendMessage(player, lm.getString(player, "Commands.Addfriend.noPlayer")
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
                lm.sendMessage(player, lm.getString(player, "Commands.Unfriend.notOwn")
                        .replace("%owner%", land.getOwnersString()));
                return;
            }

            plugin.getPlayerManager().getOffline(playerName, (offline) -> {
                if (offline == null) {
                    // Failure
                    lm.sendMessage(player, lm.getString(player, "Commands.Unfriend.noPlayer")
                            .replace("%players%", playerName));
                } else if (land.getFriends().contains(offline.getUuid())) {
                    // Success
                    String old = land.getMembersString();
                    land.removeFriend(offline.getUuid());

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            LandManageEvent landManageEvent = new LandManageEvent(player, land,
                                    "FRIENDS", old, land.getMembersString());
                            plugin.getServer().getPluginManager().callEvent(landManageEvent);
                        }
                    }.runTask(plugin);

                    lm.sendMessage(player, lm.getString(player, "Commands.Unfriend.success")
                            .replace("%players%", playerName));

                    // Let's delay it, because we cant be sure, that the requests are done when executing this piece
                    // of code.
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.getMapManager().updateAll();
                        }
                    }.runTaskLater(plugin, 60L);
                } else {
                    lm.sendMessage(player, lm.getString(player, "Commands.UnfriendAll.noFriend")
                            .replace("%player%", playerName));
                }
            });
        }
    }
}
