package biz.princeps.landlord.commands;

import biz.princeps.landlord.util.LandUtils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 16.07.17.
 */
public class Claim extends LandlordCommand {

    public void onClaim(Player player) {

        if (this.worldDisabled(player)) {
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }
        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

        // Money stuff
        double calculatedCost = calculateCost(player);
        if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), calculatedCost)) {
            plugin.getVaultHandler().take(player.getUniqueId(), calculatedCost);
            player.sendMessage(lm.getString("Commands.Claim.moneyTook")
                    .replaceAll("%money%", plugin.getVaultHandler().format(calculatedCost))
                    .replaceAll("%chunk%", LandUtils.getLandName(chunk)));

        } else {
            // NOT ENOUG MONEY
            player.sendMessage(lm.getString("Commands.Claim.notEnoughMoney")
                    .replaceAll("%money%", plugin.getVaultHandler().format(calculatedCost))
                    .replaceAll("%chunk%", LandUtils.getLandName(chunk)));
            return;
        }

        ProtectedRegion pr = plugin.getWgHandler().getRegion(chunk);
        if (pr != null) {
            player.sendMessage(lm.getString("Commands.Claim.alreadyClaimed")
                    .replaceAll("%owner%", LandUtils.printOwners(pr)));
            return;
        }

        plugin.getWgHandler().claim(chunk, player);
        player.sendMessage(lm.getString("Commands.Claim.success")
                .replaceAll("%chunk%", LandUtils.getLandName(chunk))
                .replaceAll("%world%", chunk.getWorld().getName()));

        plugin.getPlayerManager().incrementLandCount(player.getUniqueId());
    }

    private double calculateCost(Player player) {
        double minCost = plugin.getConfig().getDouble("Formula.minCost");
        double maxCost = plugin.getConfig().getDouble("Formula.maxCost");
        double multiplier = plugin.getConfig().getDouble("Formula.multiplier");
        int x = plugin.getPlayerManager().get(player.getUniqueId()).getLandCount();
        double var = Math.pow(multiplier, x);

        return maxCost - (maxCost - minCost) * var;
    }
}
