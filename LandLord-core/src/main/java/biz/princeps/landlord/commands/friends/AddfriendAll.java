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
import org.bukkit.scheduler.BukkitRunnable;

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
        if (properties.isPlayer()) {
            try {
                onAddfriend(properties.getPlayer(), arguments.get(0));
            } catch (ArgumentsOutOfBoundsException e) {
                properties.sendMessage(plugin.getLangManager().getString("Commands.AddfriendAll.noPlayer")
                        .replace("%player%", "[]"));
            }
        }
    }

    private void onAddfriend(Player player, String name) {
        if (name == null || name.isEmpty()) {
            lm.sendMessage(player, lm.getString("Commands.AddfriendAll.noPlayer")
                    .replace("%player%", name == null ? "[]" : name));
            return;
        }

        plugin.getPlayerManager().getOfflinePlayerAsync(name, lPlayer -> {
            if (lPlayer == null) {
                // Failure
                lm.sendMessage(player, lm.getString("Commands.AddfriendAll.noPlayer")
                        .replace("%player%", name));
            } else {
                int count = 0;
                for (IOwnedLand ol : plugin.getWGProxy().getRegions(player.getUniqueId())) {
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
                        .replace("%count%", count + ""));

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        plugin.getMapManager().updateAll();
                    }
                }.runTask(plugin.getPlugin());
            }
        });
    }
}

