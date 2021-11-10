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
public class UnfriendAll extends LandlordCommand {

    public UnfriendAll(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.RemovefriendAll.name"),
                plugin.getConfig().getString("CommandSettings.RemovefriendAll.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.RemovefriendAll.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.RemovefriendAll.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }
        try {
            onUnfriendall(properties.getPlayer(), arguments.get(0));
        } catch (ArgumentsOutOfBoundsException e) {
            onUnfriendall(properties.getPlayer(), null);
        }
    }

    private void onUnfriendall(Player player, String name) {
        if (isDisabledWorld(player)) {
            return;
        }

        if (name == null || name.isEmpty()) {
            lm.sendMessage(player, lm.getString(player, "Commands.Addfriend.noPlayer")
                    .replace("%players%", "?"));
            return;
        }

        plugin.getPlayerManager().getOffline(name, (offline) -> {
            if (offline == null) {
                // Failure
                lm.sendMessage(player, lm.getString(player, "Commands.UnfriendAll.noPlayer")
                        .replace("%players%", name));
            } else {
                // Success
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int count = 0;
                        for (IOwnedLand ol : plugin.getWGManager().getRegions(player.getUniqueId())) {
                            if (ol.isFriend(offline.getUuid())) {
                                String oldvalue = ol.getMembersString();
                                ol.removeFriend(offline.getUuid());
                                count++;
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        LandManageEvent landManageEvent = new LandManageEvent(player, ol,
                                                "FRIENDS", oldvalue, ol.getMembersString());
                                        plugin.getPlugin().getServer().getPluginManager().callEvent(landManageEvent);
                                    }
                                }.runTask(plugin.getPlugin());
                            }
                        }

                        if (count > 0) {
                            lm.sendMessage(player, lm.getString(player, "Commands.UnfriendAll.success")
                                    .replace("%count%", String.valueOf(count))
                                    .replace("%players%", name));

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    plugin.getMapManager().updateAll();
                                }
                            }.runTask(plugin.getPlugin());
                        } else {
                            lm.sendMessage(player, lm.getString(player, "Commands.UnfriendAll.noFriend")
                                    .replace("%player%", name));
                        }
                    }
                }.runTaskAsynchronously(plugin.getPlugin());
            }
        });
    }
}

