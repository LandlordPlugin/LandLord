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
public class AddfriendAll extends LandlordCommand {

    public AddfriendAll(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.AddfriendAll.name"),
                plugin.getConfig().getString("CommandSettings.AddfriendAll.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.AddfriendAll.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.AddfriendAll.aliases")));
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
            lm.sendMessage(player, lm.getString(player, "Commands.AddfriendAll.noPlayer")
                    .replace("%player%", "?"));
            return;
        }

        plugin.getPlayerManager().getOffline(name, (offline) -> {
            if (offline == null) {
                // Failure
                lm.sendMessage(player, lm.getString(player, "Commands.AddfriendAll.noPlayer")
                        .replace("%player%", name));
            } else if (!player.getUniqueId().equals(offline.getUuid())) {
                // Success
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int count = 0;
                        for (IOwnedLand ol : plugin.getWGManager().getRegions(player.getUniqueId())) {
                            if (!ol.isFriend(offline.getUuid())) {
                                String oldfriends = ol.getMembersString();
                                ol.addFriend(offline.getUuid());
                                count++;
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        LandManageEvent landManageEvent = new LandManageEvent(player, ol,
                                                "FRIENDS", oldfriends, ol.getMembersString());
                                        plugin.getServer().getPluginManager().callEvent(landManageEvent);
                                    }
                                }.runTask(plugin.getPlugin());
                            }
                        }

                        lm.sendMessage(player, lm.getString(player, "Commands.AddfriendAll.success")
                                .replace("%player%", name)
                                .replace("%count%", String.valueOf(count)));

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                plugin.getMapManager().updateAll();
                            }
                        }.runTask(plugin.getPlugin());
                    }
                }.runTaskAsynchronously(plugin.getPlugin());
            } else {
                lm.sendMessage(player, lm.getString(player, "Commands.Addfriend.alreadyOwn"));
            }
        });
    }
}

