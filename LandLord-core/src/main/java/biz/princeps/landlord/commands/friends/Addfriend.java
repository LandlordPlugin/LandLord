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
        try {
            onAddfriend(properties.getPlayer(), arguments.get(0));
        } catch (ArgumentsOutOfBoundsException e) {
            onAddfriend(properties.getPlayer(), null);
        }
    }

    private void onAddfriend(Player player, String playerName) {
        if (isDisabledWorld(player)) {
            return;
        }

        IOwnedLand land = plugin.getWGManager().getRegion(player.getLocation().getChunk());

        if (land != null) {
            if (!land.isOwner(player.getUniqueId()) && !player.hasPermission("landlord.admin.modifyfriends")) {
                lm.sendMessage(player, lm.getString(player, "Commands.Addfriend.notOwn")
                        .replace("%owner%", land.getOwnersString()));
                return;
            }

            if (playerName == null) {
                lm.sendMessage(player, lm.getString(player, "Commands.Addfriend.noPlayer")
                        .replace("%players%", "?"));
                return;
            }

            plugin.getPlayerManager().getOffline(playerName, (offline) -> {
                if (offline == null) {
                    // Failure
                    lm.sendMessage(player, lm.getString(player, "Commands.Addfriend.noPlayer")
                            .replace("%players%", playerName));
                } else if (!land.isOwner(offline.getUuid())) {
                    // Success
                    String oldFriends = land.getMembersString();
                    land.addFriend(offline.getUuid());

                    Bukkit.getScheduler().runTask(plugin.getPlugin(), () -> {
                        LandManageEvent landManageEvent = new LandManageEvent(player, land,
                                "FRIENDS", oldFriends, land.getMembersString());
                        Bukkit.getPluginManager().callEvent(landManageEvent);
                    });

                    lm.sendMessage(player, lm.getString(player, "Commands.Addfriend.success")
                            .replace("%players%", playerName));

                    // lets delay it, because we cant be sure, that the requests are done when executing this piece
                    // of code
                    Bukkit.getScheduler().runTaskLater(plugin.getPlugin(), plugin.getMapManager()::updateAll, 60L);
                } else {
                    lm.sendMessage(player, lm.getString(player, "Commands.Addfriend.alreadyOwn"));
                }
            });
        }
    }
}