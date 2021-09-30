package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.landlord.api.IMultiTaskManager;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IPlayerManager;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.multi.MultiClearInactiveTask;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClearInactive extends LandlordCommand {

    private final ILangManager lgManager;
    private final IPlayerManager playerManager;
    private final IMultiTaskManager multiTaskManager;

    public ClearInactive(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.ClearInactive.name"),
                pl.getConfig().getString("CommandSettings.ClearInactive.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.ClearInactive.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.ClearInactive.aliases")));
        this.lgManager = pl.getLangManager();
        this.playerManager = pl.getPlayerManager();
        this.multiTaskManager = pl.getMultiTaskManager();
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (arguments.size() == 1) {
            // Clear inactive players' lands
            try {
                int minInactiveDays = arguments.getInt(0);
                if (properties.getCommandSender().hasPermission("landlord.admin.clearinactive"))
                    clearInactive(minInactiveDays, properties.getCommandSender());
            } catch (NumberFormatException e) {
                properties.sendUsage();
            }
        } else {
            properties.sendUsage();
        }
    }

    private void clearInactive(int minInactiveDays, CommandSender sender) {
        lgManager.sendMessage(sender, lgManager.getString("Commands.ClearInactive.info")
                .replace("%inactivity%", String.valueOf(minInactiveDays)));

        Bukkit.getScheduler().runTaskAsynchronously(plugin.getPlugin(), () -> {
            OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
            List<OfflinePlayer> playersToClear = new ArrayList<>(offlinePlayers.length);

            for (OfflinePlayer offlinePlayer : offlinePlayers) {
                IPlayer lPlayer = playerManager.getOfflineSync(offlinePlayer.getUniqueId());

                if (lPlayer != null) {
                    Duration offlineInterval = Duration.between(lPlayer.getLastSeen(), LocalDateTime.now());
                    // Calculates if the offlineInterval is positive, i.e. player is inactive because offlineInterval
                    // exceeds minInactiveDays.
                    boolean isInactive = offlineInterval.compareTo(Duration.ofDays(minInactiveDays)) > 0;

                    if (isInactive) {
                        playersToClear.add(offlinePlayer);
                    }
                }
            }

            multiTaskManager.enqueueTask(new MultiClearInactiveTask(plugin, sender, playersToClear, minInactiveDays));
        });
    }

}
