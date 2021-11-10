package biz.princeps.landlord.multi;

import biz.princeps.landlord.api.AMultiTask;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.events.LandClearInactiveEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class MultiClearInactiveTask extends AMultiTask<OfflinePlayer> {

    private final IWorldGuardManager wgManager;
    private final ILangManager lgManager;
    private final CommandSender commandSender;

    private final long minInactiveDays;
    private final int clearedPlayers;
    private int clearedLands;

    public MultiClearInactiveTask(ILandLord plugin, CommandSender commandSender, Collection<OfflinePlayer> operations, int minInactiveDays) {
        super(plugin, operations);

        this.wgManager = plugin.getWGManager();
        this.lgManager = plugin.getLangManager();
        this.commandSender = commandSender;

        this.minInactiveDays = minInactiveDays;
        this.clearedPlayers = operations.size();
        this.clearedLands = 0;
    }

    @Override
    public boolean process(OfflinePlayer offlinePlayer) {
        LandClearInactiveEvent event = new LandClearInactiveEvent(commandSender, offlinePlayer);
        plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            clearedLands += wgManager.unclaim(
                    wgManager.getRegions(offlinePlayer.getUniqueId()));
        }

        return true;
    }

    @Override
    public void complete() {
        lgManager.sendMessage(commandSender, lgManager.getString("Commands.ClearInactive.success")
                .replace("%players%", String.valueOf(clearedPlayers))
                .replace("%lands%", String.valueOf(clearedLands))
                .replace("%inactivity%", String.valueOf(minInactiveDays)));
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getMapManager().updateAll();
            }
        }.runTask(plugin.getPlugin());
    }

}
