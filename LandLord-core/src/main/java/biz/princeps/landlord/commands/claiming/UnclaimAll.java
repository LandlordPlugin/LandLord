package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMultiTaskManager;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.ManageMode;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.multi.MultiUnclaimTask;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/07/17
 * <p>
 */
public class UnclaimAll extends LandlordCommand {

    private final IMultiTaskManager multiTaskManager;

    public UnclaimAll(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.UnclaimAll.name"),
                pl.getConfig().getString("CommandSettings.UnclaimAll.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.UnclaimAll.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.UnclaimAll.aliases")));
        this.multiTaskManager = plugin.getMultiTaskManager();
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        final Player player = properties.getPlayer();
        final List<World> worlds;

        if (arguments.size() == 1) {
            final String worldName = arguments.get(0);
            final World world = Bukkit.getWorld(worldName);

            if (world == null) {
                lm.sendMessage(player, lm.getString(player, "Commands.UnclaimAll.invalidWorld"));
                return;
            } else {
                worlds = Collections.singletonList(world);
            }
        } else {
            worlds = Bukkit.getWorlds();
        }

        if (plugin.getConfig().getBoolean("ConfirmationDialog.onUnclaimAll")) {
            final String guiMsg = lm.getRawString("Commands.UnclaimAll.confirm");

            PrincepsLib.getConfirmationManager().drawGUI(player, guiMsg,
                    (p) -> {
                        performUnclaimAll(player, worlds);
                        player.closeInventory();
                    },
                    (p2) -> player.closeInventory(), null);
        } else {
            performUnclaimAll(player, worlds);
        }
    }

    public void performUnclaimAll(Player player, List<World> worlds) {
        for (World world : worlds) {
            if (isDisabledWorld(world)) {
                continue;
            }

            final Set<IOwnedLand> playerLands = new HashSet<>(plugin.getWGManager().getRegions(player.getUniqueId(), world));
            if (playerLands.isEmpty()) {
                lm.sendMessage(player, lm.getString(player, "Commands.UnclaimAll.notOwnFreeLand") + " (" + world.getName() + ")");
                continue;
            }

            multiTaskManager.enqueueTask(new MultiUnclaimTask(plugin, player, playerLands, ManageMode.ALL));
        }
    }

}
