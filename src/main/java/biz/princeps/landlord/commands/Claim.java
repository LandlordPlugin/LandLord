package biz.princeps.landlord.commands;

import biz.princeps.landlord.util.OwnedLand;
import org.bukkit.Chunk;
import org.bukkit.Particle;
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
        OwnedLand pr = plugin.getWgHandler().getRegion(chunk);
        if (pr != null) {
            player.sendMessage(lm.getString("Commands.Claim.alreadyClaimed")
                    .replaceAll("%owner%", pr.printOwners()));
            return;
        }

        // Money stuff
        double calculatedCost = OwnedLand.calculateCost(player);
        if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), calculatedCost)) {
            plugin.getVaultHandler().take(player.getUniqueId(), calculatedCost);
            player.sendMessage(lm.getString("Commands.Claim.moneyTook")
                    .replaceAll("%money%", plugin.getVaultHandler().format(calculatedCost))
                    .replaceAll("%chunk%", OwnedLand.getLandName(chunk)));

        } else {
            // NOT ENOUG MONEY
            player.sendMessage(lm.getString("Commands.Claim.notEnoughMoney")
                    .replaceAll("%money%", plugin.getVaultHandler().format(calculatedCost))
                    .replaceAll("%chunk%", OwnedLand.getLandName(chunk)));
            return;
        }

        plugin.getWgHandler().claim(chunk, player);
        player.sendMessage(lm.getString("Commands.Claim.success")
                .replaceAll("%chunk%", OwnedLand.getLandName(chunk))
                .replaceAll("%world%", chunk.getWorld().getName()));

        OwnedLand.highlightLand(player, Particle.VILLAGER_HAPPY);

        plugin.getMapManager().updateAll();
    }


}
