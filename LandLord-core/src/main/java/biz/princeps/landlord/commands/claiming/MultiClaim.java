package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class MultiClaim extends LandlordCommand {

    private Claim claim = new Claim(plugin, true);

    public MultiClaim(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.MultiClaim.name"),
                pl.getConfig().getString("CommandSettings.MultiClaim.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.MultiClaim.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.MultiClaim.aliases")));
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
        String confirmcmd = "/" + plugin.getConfig().getString("CommandSettings.Main.name") + " confirm";

        try {
            MultiClaimMode mode = MultiClaimMode.valueOf(arguments.get()[0].toUpperCase());
            int param = arguments.getInt(1);

            Set<Chunk> toClaim = getToClaimChunks(mode, param, player.getLocation());

            if (toClaim.size() == 0) {
                lm.sendMessage(player, lm.getString("Commands.MultiClaim.noLands"));
                return;
            }

            int initalRegionCount = plugin.getWGProxy().getRegionCount(player.getUniqueId());
            double cost = 0;
            for (Chunk ignored : toClaim) {
                cost += plugin.getCostManager().calculateCost(initalRegionCount);
                initalRegionCount++;
            }

            String formattedCost = (Options.isVaultEnabled() ? plugin.getVaultManager().format(cost) : "");

            PrincepsLib.getConfirmationManager().draw(player,
                    lm.getRawString("Commands.MultiClaim.guiMessage")
                            .replace("%amount%", toClaim.size() + "")
                            .replace("%cost%", formattedCost),
                    lm.getString("Commands.MultiClaim.chatMessage")
                            .replace("%amount%", toClaim.size() + "")
                            .replace("%cost%", formattedCost),
                    (p) -> {
                        // on accept
                        toClaim.forEach(cl -> claim.onClaim(player, cl));
                        p.closeInventory();
                    },
                    (p) -> {
                        // on decline
                        lm.sendMessage(p, lm.getString("Commands.MultiClaim.abort")
                                .replace("%amount%", toClaim.size() + ""));
                        p.closeInventory();
                    }, confirmcmd);
        } catch (IllegalArgumentException | ArgumentsOutOfBoundsException ex) {
            properties.sendUsage();
        }
    }

    private Set<Chunk> getToClaimChunks(MultiClaimMode mode, int param, Location center) {
        Set<Chunk> toClaim = new HashSet<>();
        switch (mode) {
            case CIRCULAR:
                //TODO implement circular multiclaim
                break;
            case RECTANGULAR:
                int xCenter = center.getChunk().getX();
                int zCenter = center.getChunk().getZ();
                for (int x = xCenter - param; x <= xCenter + param; x++) {
                    for (int z = zCenter - param; z <= zCenter + param; z++) {
                        Chunk chunk = center.getWorld().getChunkAt(x, z);
                        if (plugin.getWGProxy().getRegion(chunk) == null) {
                            toClaim.add(chunk);
                        }
                    }
                }
                break;
        }
        return toClaim;
    }


    public enum MultiClaimMode {
        CIRCULAR, RECTANGULAR
    }
}