package biz.princeps.landlord.commands;

import biz.princeps.landlord.crossversion.CParticle;
import biz.princeps.landlord.util.OwnedLand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Chunk;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

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
                    .replace("%owner%", pr.printOwners()));
            return;
        }

        int regionCount = plugin.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegionCountOfPlayer(plugin.getWgHandler().getWG().wrapPlayer(player));
        List<Integer> limitlist = plugin.getConfig().getIntegerList("limits");

        if (!player.hasPermission("landlord.limit.override")) {

            int highestAllowedLandCount = -1;
            for (Integer integer : limitlist) {
                if (regionCount <= integer)
                    if (player.hasPermission("landlord.limit." + integer)) {
                        highestAllowedLandCount = integer;
                    }
            }

            if (regionCount >= highestAllowedLandCount) {
                player.sendMessage(lm.getString("Commands.Claim.hardcap").replace("%regions%", highestAllowedLandCount + ""));
                return;
            }
        }

        if (plugin.getConfig().getBoolean("Shop.enable")) {
            int claims = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();

            if (regionCount >= claims) {
                ComponentBuilder builder = new ComponentBuilder(lm.getString("Commands.Claim.limit")
                        .replace("%regions%", regionCount + "")
                        .replace("%claims%", claims + ""))
                        .color(ChatColor.YELLOW)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ll shop"));
                player.spigot().sendMessage(builder.create());
                return;
            }
        }

        // Money stuff
        double calculatedCost = OwnedLand.calculateCost(player);
        if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), calculatedCost)) {
            plugin.getVaultHandler().take(player.getUniqueId(), calculatedCost);
            player.sendMessage(lm.getString("Commands.Claim.moneyTook")
                    .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                    .replace("%chunk%", OwnedLand.getLandName(chunk)));

        } else {
            // NOT ENOUG MONEY
            player.sendMessage(lm.getString("Commands.Claim.notEnoughMoney")
                    .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                    .replace("%chunk%", OwnedLand.getLandName(chunk)));
            return;
        }

        plugin.getWgHandler().claim(chunk, player.getUniqueId());
        player.sendMessage(lm.getString("Commands.Claim.success")
                .replace("%chunk%", OwnedLand.getLandName(chunk))
                .replace("%world%", chunk.getWorld().getName()));

        OwnedLand.highlightLand(player, CParticle.VILLAGERHAPPY);

        plugin.getMapManager().updateAll();
    }


}
