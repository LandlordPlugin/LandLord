package biz.princeps.landlord.multi;

import biz.princeps.landlord.api.AMultiTask;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IPlayerManager;
import biz.princeps.landlord.api.IWorldGuardManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Iterator;

public class MultiClearInactiveTask extends AMultiTask<IOwnedLand> {

    private final IWorldGuardManager wgManager;
    private final IPlayerManager playerManager;
    private final ILangManager lgManager;
    private final CommandSender commandSender;

    private final IPlayer target;
    private final long inactiveForDays;
    private final int clearedLands;

    public MultiClearInactiveTask(ILandLord plugin, CommandSender commandSender, Collection<IOwnedLand> operations, IPlayer target, long inactiveForDays) {
        super(plugin, operations);

        this.wgManager = plugin.getWGManager();
        this.playerManager = plugin.getPlayerManager();
        this.lgManager = plugin.getLangManager();
        this.commandSender = commandSender;

        this.target = target;
        this.inactiveForDays = inactiveForDays;
        this.clearedLands = operations.size();
    }

    @Override
    public int processOperations(int limit) {
        int iterations = 0;

        target.setHome(null);
        playerManager.save(target, true);

        for (Iterator<IOwnedLand> iterator = queue.iterator(); iterator.hasNext() && iterations < limit; ) {
            IOwnedLand ownedLand = iterator.next();
            wgManager.unclaim(ownedLand);

            if (!iterator.hasNext()) {
                lgManager.sendMessage(commandSender, lgManager.getString("Commands.ClearInactive.success")
                        .replace("%count%", String.valueOf(clearedLands))
                        .replace("%player%", target.getName())
                        .replace("%inactivity%", String.valueOf(inactiveForDays)));

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin.getPlugin(), () -> plugin.getMapManager().updateAll());
            }

            iterator.remove();
            iterations++;
        }

        return iterations;
    }

}
