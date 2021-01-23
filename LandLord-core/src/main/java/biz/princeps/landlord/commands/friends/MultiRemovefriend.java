package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.commands.MultiMode;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MultiRemovefriend extends LandlordCommand {

    private final IWorldGuardManager wg;

    public MultiRemovefriend(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.MultiRemovefriend.name"),
                pl.getConfig().getString("CommandSettings.MultiRemovefriend.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.MultiRemovefriend.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.MultiRemovefriend.aliases")));
        this.wg = plugin.getWGManager();
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            properties.sendMessage("Player command only!");
            return;
        }
        if (arguments.size() != 3) {
            properties.sendUsage();
            return;
        }
        MultiMode mode;
        int radius;
        String name;
        try {
            mode = MultiMode.valueOf(arguments.get()[0].toUpperCase());
            radius = arguments.getInt(1);
            name = arguments.get(2);
        } catch (IllegalArgumentException | ArgumentsOutOfBoundsException ex) {
            properties.sendUsage();
            return;
        }

        onUnfriend(properties.getPlayer(), mode, radius, name);
    }

    private void onUnfriend(Player player, MultiMode mode, int radius, String name) {
        if (isDisabledWorld(player)) {
            return;
        }

        if (name == null || name.isEmpty()) {
            lm.sendMessage(player, lm.getString(player, "Commands.MultiUnfriend.noPlayer")
                    .replace("%players%", "?"));
            return;
        }

        plugin.getPlayerManager().getOffline(name, (offline) -> {
            if (offline == null) {
                // Failure
                lm.sendMessage(player, lm.getString(player, "Commands.MultiUnfriend.noPlayer")
                        .replace("%players%", name));
            } else {
                // Success
                Bukkit.getScheduler().runTaskAsynchronously(plugin.getPlugin(), () -> {
                    int count = 0;
                    for (IOwnedLand ol : mode.getLandsOf(radius, player.getLocation(), player.getUniqueId(), wg)) {
                        if (ol.isFriend(offline.getUuid())) {
                            String oldvalue = ol.getMembersString();
                            ol.removeFriend(offline.getUuid());
                            count++;
                            Bukkit.getScheduler().runTask(plugin.getPlugin(), () -> {
                                LandManageEvent landManageEvent = new LandManageEvent(player, ol,
                                        "FRIENDS", oldvalue, ol.getMembersString());
                                Bukkit.getPluginManager().callEvent(landManageEvent);
                            });
                        }
                    }

                    if (count > 0) {
                        lm.sendMessage(player, lm.getString(player, "Commands.MultiUnfriend.success")
                                .replace("%count%", String.valueOf(count))
                                .replace("%players%", name));

                        Bukkit.getScheduler().runTask(plugin.getPlugin(), plugin.getMapManager()::updateAll);
                    } else {
                        lm.sendMessage(player, lm.getString(player, "Commands.MultiUnfriend.noFriend")
                                .replace("%player%", name));
                    }
                });
            }
        });
    }

}

