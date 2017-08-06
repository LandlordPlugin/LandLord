package biz.princeps.landlord.commands;

import biz.princeps.landlord.api.events.LandClaimEvent;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.crossversion.CParticle;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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

        // Check if there is an overlapping wg-region
        if (!plugin.getWgHandler().canClaim(player, chunk)) {
            LandClaimEvent event = new LandClaimEvent(player, pr, LandClaimEvent.ClaimState.OVERLAPPINGREGION);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                player.sendMessage(lm.getString("Commands.Claim.notAllowed"));
                return;
            }
        }

        if (pr != null) {
            LandClaimEvent event = new LandClaimEvent(player, pr, LandClaimEvent.ClaimState.ALREADYCLAIMED);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                player.sendMessage(lm.getString("Commands.Claim.alreadyClaimed")
                        .replace("%owner%", pr.printOwners()));
                return;
            }
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

        if (plugin.getConfig().getBoolean("Shop.enable") && plugin.isVaultEnabled()) {
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
        if (plugin.isVaultEnabled()) {
            double calculatedCost = OwnedLand.calculateCost(player);
            if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), calculatedCost)) {
                plugin.getVaultHandler().take(player.getUniqueId(), calculatedCost);
                if (calculatedCost > 0)
                    player.sendMessage(lm.getString("Commands.Claim.moneyTook")
                            .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                            .replace("%chunk%", OwnedLand.getLandName(chunk)));

            } else {
                // NOT ENOUG MONEY
                LandClaimEvent event = new LandClaimEvent(player, pr, LandClaimEvent.ClaimState.NOTENOUGHMONEY);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    player.sendMessage(lm.getString("Commands.Claim.notEnoughMoney")
                            .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                            .replace("%chunk%", OwnedLand.getLandName(chunk)));
                    return;
                }
            }
        }

        LandClaimEvent event = new LandClaimEvent(player, pr, LandClaimEvent.ClaimState.SUCCESS);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            plugin.getWgHandler().claim(chunk, player.getUniqueId());
            player.sendMessage(lm.getString("Commands.Claim.success")
                    .replace("%chunk%", OwnedLand.getLandName(chunk))
                    .replace("%world%", chunk.getWorld().getName()));

            OwnedLand.highlightLand(player, CParticle.VILLAGERHAPPY);

            plugin.getMapManager().updateAll();
        }
    }


}

