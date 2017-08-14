package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.commands.LandlordCommand;
import co.aikar.commands.CommandIssuer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class GiveClaims extends LandlordCommand {

    // ll giveclaims name price amount
    public void onGiveClaims(CommandIssuer issuer, String target, double cost, int amount) {
        Player player = Bukkit.getPlayer(target);
        if (player == null) {
            issuer.sendMessage(lm.getString("Commands.GiveClaims.noPlayer"));
            return;
        }

        if (!player.hasPermission("landlord.limit.override")) {
            // int regionCount = pl.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegionCountOfPlayer(pl.getWgHandler().getWG().wrapPlayer(player));
            int claimcount = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();
            List<Integer> limitlist = plugin.getConfig().getIntegerList("limits");

            int highestAllowedLandCount = -1;
            for (Integer integer : limitlist) {
                if (claimcount + amount <= integer) {
                    if (player.hasPermission("landlord.limit." + integer)) {
                        highestAllowedLandCount = integer;
                    }
                }
            }
            if (claimcount + amount > highestAllowedLandCount) {
                player.sendMessage(plugin.getLangManager().getString("Shop.notAllowed"));
                return;
            }
        }


        if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), cost)) {
            plugin.getVaultHandler().take(player.getUniqueId(), cost);
            plugin.getPlayerManager().get(player.getUniqueId()).addClaims(amount);
            player.sendMessage(plugin.getLangManager().getString("Shop.success")
                    .replace("%number%", amount + "")
                    .replace("%cost%", plugin.getVaultHandler().format(cost)));
            player.closeInventory();
        } else {
            player.sendMessage(plugin.getLangManager().getString("Shop.notEnoughMoney")
                    .replace("%number%", amount + "")
                    .replace("%cost%", plugin.getVaultHandler().format(cost)));
            player.closeInventory();
        }
    }

}
