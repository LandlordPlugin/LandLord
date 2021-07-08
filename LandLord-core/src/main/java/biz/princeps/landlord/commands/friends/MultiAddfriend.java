package biz.princeps.landlord.commands.friends;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.multi.MultiMode;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MultiAddfriend extends LandlordCommand {

    private final IWorldGuardManager wg;

    public MultiAddfriend(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.MultiAddfriend.name"),
                pl.getConfig().getString("CommandSettings.MultiAddfriend.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.MultiAddfriend.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.MultiAddfriend.aliases")));
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

        onAddfriend(properties.getPlayer(), mode, radius, name);
    }

    private void onAddfriend(Player player, MultiMode mode, int radius, String name) {
        if (isDisabledWorld(player)) {
            return;
        }

        if (name == null || name.isEmpty()) {
            lm.sendMessage(player, lm.getString(player, "Commands.MultiAddfriend.noPlayer")
                    .replace("%player%", "?"));
            return;
        }

        plugin.getPlayerManager().getOffline(name, (offline) -> {
            if (offline == null) {
                // Failure
                lm.sendMessage(player, lm.getString(player, "Commands.MultiAddfriend.noPlayer")
                        .replace("%player%", name));
            } else if (!player.getUniqueId().equals(offline.getUuid())) {
                // Success
                Bukkit.getScheduler().runTaskAsynchronously(plugin.getPlugin(), () -> {
                    int count = 0;
                    for (IOwnedLand ol : mode.getLandsOf(radius, player.getLocation(), player.getUniqueId(), wg)) {
                        if (!ol.isFriend(offline.getUuid())) {
                            String oldfriends = ol.getMembersString();
                            ol.addFriend(offline.getUuid());
                            count++;
                            Bukkit.getScheduler().runTask(plugin.getPlugin(), () -> {
                                LandManageEvent landManageEvent = new LandManageEvent(player, ol,
                                        "FRIENDS", oldfriends, ol.getMembersString());
                                Bukkit.getPluginManager().callEvent(landManageEvent);
                            });
                        }
                    }

                    lm.sendMessage(player, lm.getString(player, "Commands.MultiAddfriend.success")
                            .replace("%player%", name)
                            .replace("%count%", String.valueOf(count)));

                    Bukkit.getScheduler().runTask(plugin.getPlugin(), plugin.getMapManager()::updateAll);
                });
            } else {
                lm.sendMessage(player, lm.getString(player, "Commands.MultiAddfriend.alreadyOwn"));
            }
        });
    }
}

