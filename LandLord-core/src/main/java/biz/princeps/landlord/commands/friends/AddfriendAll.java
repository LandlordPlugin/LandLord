package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.api.ILandLord;
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
public class AddfriendAll extends LandlordCommand {

    public AddfriendAll(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.AddfriendAll.name"),
                pl.getConfig().getString("CommandSettings.AddfriendAll.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.AddfriendAll.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.AddfriendAll.aliases")));
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

    private void onAddfriend(Player player, String name) {
        if (isDisabledWorld(player)) {
            return;
        }

        if (name == null || name.isEmpty()) {
            lm.sendMessage(player, lm.getString("Commands.AddfriendAll.noPlayer")
                    .replace("%player%", "?"));
            return;
        }

        plugin.getPlayerManager().getOfflinePlayerAsync(name, lPlayer -> {
            if (lPlayer == null) {
                // Failure
                lm.sendMessage(player, lm.getString("Commands.AddfriendAll.noPlayer")
                        .replace("%player%", name));
            } else if (!player.getUniqueId().equals(lPlayer.getUuid())) {
                // Success
                int count = 0;
                for (IOwnedLand ol : plugin.getWGManager().getRegions(player.getUniqueId())) {
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
                        .replace("%count%", String.valueOf(count)));

                Bukkit.getScheduler().runTask(plugin.getPlugin(), plugin.getMapManager()::updateAll);
            } else {
                lm.sendMessage(player, lm.getString("Commands.Addfriend.alreadyOwn"));
            }
        });
    }
}

