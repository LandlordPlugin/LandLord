package biz.princeps.landlord.multi;

import biz.princeps.landlord.api.AMultiTask;
import biz.princeps.landlord.api.ClearType;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IPlayerManager;
import biz.princeps.landlord.api.IWorldGuardManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class MultiClearTask extends AMultiTask<IOwnedLand> {

    private final IWorldGuardManager wgManager;
    private final IPlayerManager playerManager;
    private final ILangManager lgManager;
    private final CommandSender commandSender;

    private final String targetName;
    private final int clearedLands;
    private final Set<UUID> clearedHomes;
    private final ClearType clearType;

    public MultiClearTask(ILandLord plugin, CommandSender commandSender, Collection<IOwnedLand> operations, String targetName, ClearType clearType) {
        super(plugin, operations);

        this.wgManager = plugin.getWGManager();
        this.playerManager = plugin.getPlayerManager();
        this.lgManager = plugin.getLangManager();
        this.commandSender = commandSender;

        this.targetName = targetName;
        this.clearedLands = operations.size();
        this.clearedHomes = new HashSet<>();
        this.clearType = clearType;
    }

    @Override
    public int processOperations(int limit) {
        int iterations = 0;

        for (Iterator<IOwnedLand> iterator = queue.iterator(); iterator.hasNext() && iterations < limit; ) {
            IOwnedLand ownedLand = iterator.next();
            UUID owner = ownedLand.getOwner();

            if (clearType == ClearType.WORLD && !clearedHomes.contains(owner)) {
                if (plugin.isDisabling()) {
                    IPlayer lPlayer = playerManager.getOfflineSync(owner);
                    processHome(lPlayer);
                } else {
                    playerManager.getOffline(owner, this::processHome);
                }
            }

            wgManager.unclaim(ownedLand);

            if (!iterator.hasNext() && !plugin.isDisabling()) {
                switch (clearType) {
                    case PLAYER:
                        plugin.getPlayerManager().getOffline(targetName, lPlayer -> {
                            lPlayer.setHome(null);
                            plugin.getPlayerManager().save(lPlayer, true);
                        });

                        lgManager.sendMessage(commandSender, lgManager.getString("Commands.Clear.gui.clearplayer.success")
                                .replace("%count%", String.valueOf(clearedLands))
                                .replace("%player%", targetName));
                        break;
                    case WORLD:
                        lgManager.sendMessage(commandSender, lgManager.getString("Commands.Clear.gui.clearworld.success")
                                .replace("%count%", String.valueOf(clearedLands))
                                .replace("%world%", targetName));
                        break;
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin.getPlugin(), () -> plugin.getMapManager().updateAll());
            }

            iterator.remove();
            iterations++;
        }

        return iterations;
    }

    private void processHome(IPlayer lPlayer) {
        Location home = lPlayer.getHome();
        if (home != null && home.getWorld().getName().equals(targetName)) {
            lPlayer.setHome(null);
            clearedHomes.add(lPlayer.getUuid());
            playerManager.save(lPlayer, !plugin.isDisabling());
        }
    }

}
