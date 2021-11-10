package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMultiTaskManager;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.ManageMode;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.multi.MultiMode;
import biz.princeps.landlord.multi.MultiUnclaimTask;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

import java.util.Set;

public class MultiUnclaim extends LandlordCommand {

    private final IWorldGuardManager wg;
    private final IMultiTaskManager multiTaskManager;

    public MultiUnclaim(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.MultiUnclaim.name"),
                plugin.getConfig().getString("CommandSettings.MultiUnclaim.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.MultiUnclaim.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.MultiUnclaim.aliases")));
        this.wg = plugin.getWGManager();
        this.multiTaskManager = plugin.getMultiTaskManager();
    }

    /**
     * Executed when a player enters /land multiunclaim
     * Expected parameters is
     * /land multiunclaim {@code <option>}
     * Option is either circular or rectangular!
     * <p>
     * All the individual claims are redirected to the function that handles /land claim
     *
     * @param properties the player who wants to claim
     * @param arguments  option
     */
    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            properties.sendMessage("Player command only!");
            return;
        }
        if (arguments.size() != 2) {
            properties.sendUsage();
            return;
        }

        Player player = properties.getPlayer();
        if (isDisabledWorld(player)) {
            return;
        }

        MultiMode mode;
        int radius;
        try {
            mode = MultiMode.valueOf(arguments.get(0).toUpperCase());
            radius = arguments.getInt(1);
        } catch (IllegalArgumentException | ArgumentsOutOfBoundsException ex) {
            properties.sendUsage();
            return;
        }

        if (plugin.getConfig().getBoolean("ConfirmationDialog.onMultiUnclaim")) {
            String guiMsg = lm.getRawString("Commands.MultiUnclaim.confirm");

            PrincepsLib.getConfirmationManager().drawGUI(player, guiMsg,
                    (p) -> {
                        performMultiUnclaim(player, mode, radius);
                        player.closeInventory();
                    },
                    (p) -> player.closeInventory(), null);
        } else {
            performMultiUnclaim(player, mode, radius);
        }
    }

    public void performMultiUnclaim(Player player, MultiMode mode, int radius) {
        int maxSize = plugin.getPlugin().getServer().getViewDistance() + 2;

        // Avoid latencies with MultiUnclaim, because World#getChunk method may generate the chunk :/
        if (radius > maxSize) { // +2 for marge value. Unless server has a huge render distance (16 for example), won't cause any trouble
            lm.sendMessage(player, lm.getString(player, "Commands.MultiUnclaim.hugeSize")
                    .replace("%max_size%", maxSize + ""));
            return;
        }

        Set<IOwnedLand> toUnclaim = mode.getLandsOf(radius, player.getLocation(), player.getUniqueId(), wg);
        if (toUnclaim.isEmpty()) {
            lm.sendMessage(player, lm.getString(player, "Commands.MultiUnclaim.notOwnFreeLand"));
            return;
        }

        multiTaskManager.enqueueTask(new MultiUnclaimTask(plugin, player, toUnclaim, player.getWorld(), ManageMode.MULTI));
    }

}
