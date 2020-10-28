package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.landlord.commands.MultiMode;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.Set;

public class MultiClaim extends LandlordCommand {

    private final IWorldGuardManager wg;
    private final Claim claim = new Claim(plugin, true);

    public MultiClaim(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.MultiClaim.name"),
                pl.getConfig().getString("CommandSettings.MultiClaim.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.MultiClaim.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.MultiClaim.aliases")));
        this.wg = plugin.getWGManager();
    }

    /**
     * Executed when a player enters /land multiclaim
     * Expected parameters is
     * /land multiclaim <option>
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
        String confirmcmd = PrincepsLib.getCommandManager().getCommand(Landlordbase.class)
                .getCommandString(Landlordbase.Confirm.class);
        MultiMode mode;
        int radius;
        try {
            mode = MultiMode.valueOf(arguments.get()[0].toUpperCase());
            radius = arguments.getInt(1);
        } catch (IllegalArgumentException | ArgumentsOutOfBoundsException ex) {
            properties.sendUsage();
            return;
        }

        final int maxSize = Bukkit.getViewDistance() + 2;

        // Implementation of this to avoid latencies with MultiClaim, because getChunk methode generates the chunk if is not :/
        if (radius > maxSize) { // +2 for marge value. Unless server has a huge render distance (16 for example), won't cause any trouble
            lm.sendMessage(player, lm.getString("Commands.MultiClaim.hugeSize")
                    .replace("%max_size%", maxSize + ""));
            return;
        }

        final Set<Chunk> toClaim = mode.getFreeLands(radius, player.getLocation(), wg);

        if (toClaim.size() == 0) {
            lm.sendMessage(player, lm.getString("Commands.MultiClaim.noLands"));
            return;
        }

        int initalRegionCount = plugin.getWGManager().getRegionCount(player.getUniqueId());
        double cost = 0;
        for (int i = 0; i < toClaim.size(); i++) {
            cost += plugin.getCostManager().calculateCost(initalRegionCount);
            initalRegionCount++;
        }

        String formattedCost = (Options.isVaultEnabled() ? plugin.getVaultManager().format(cost) : "");
        if (plugin.getConfig().getBoolean("ConfirmationDialog.onMultiClaim")) {
            PrincepsLib.getConfirmationManager().draw(player,
                    lm.getRawString("Commands.MultiClaim.guiMessage")
                            .replace("%amount%", toClaim.size() + "")
                            .replace("%cost%", formattedCost),
                    lm.getString("Commands.MultiClaim.chatMessage")
                            .replace("%amount%", toClaim.size() + "")
                            .replace("%cost%", formattedCost),
                    (p) -> {
                        // on accept
                        for (Chunk chunk : toClaim) {
                            claim.onClaim(player, chunk);
                        }
                        p.closeInventory();
                    },
                    (p) -> {
                        // on decline
                        lm.sendMessage(p, lm.getString("Commands.MultiClaim.abort")
                                .replace("%amount%", toClaim.size() + ""));
                        p.closeInventory();
                    }, confirmcmd);
        } else {
            for (Chunk chunk : toClaim) {
                claim.onClaim(player, chunk);
            }
        }
    }

}
