package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class GiveClaims extends LandlordCommand {

    public void onGiveClaims(Properties issuer, Arguments args) {
        String target;
        int amount;
        double cost = -1;

        switch (args.size()) {
            case 3:
                // ll giveclaims name price amount
                try {
                    target = args.get(0);
                    cost = args.getDouble(1);
                    amount = args.getInt(2);
                } catch (ArgumentsOutOfBoundsException e) {
                    e.printStackTrace();
                    return;
                }

                Player player = Bukkit.getPlayer(target);

                if (player != null) {
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


                addClaims(issuer, target, amount);
                break;

            case 2:
                //ll giveclaims name amount
                try {
                    target = args.get(0);
                    amount = args.getInt(1);
                } catch (ArgumentsOutOfBoundsException e) {
                    e.printStackTrace();
                    return;
                }
                if (issuer.isPlayer()) {
                    checkPermission(issuer.getPlayer(), amount);

                    addClaims(issuer, target, amount);
                }
                break;

            case 1:
                //ll giveclaims amount
                try {
                    amount = args.getInt(0);
                } catch (ArgumentsOutOfBoundsException e) {
                    e.printStackTrace();
                    return;
                }

                if (issuer.isPlayer()) {
                    checkPermission(issuer.getPlayer(), amount);
                    addClaims(issuer, issuer.getPlayer().getName(), amount);
                } else {
                    issuer.sendUsage();
                }
                break;

            default:
                issuer.sendUsage();
        }
    }

    private void addClaims(Properties issuer, String target, int amount) {

        plugin.getPlayerManager().getOfflinePlayerAsync(target, p -> {
            if (p != null) {
                p.addClaims(amount);
                plugin.getPlayerManager().saveSync(p);
                if (Bukkit.getOfflinePlayer(p.getUuid()).isOnline()) {
                    Bukkit.getPlayer(p.getUuid()).sendMessage(lm.getString("Commands.GiveClaims.success").replace("%amount%", String.valueOf(amount)));
                }
            } else {
                issuer.sendMessage(lm.getString("Commands.GiveClaims.noPlayer"));
            }
        });
    }

    public boolean checkPermission(Player player, int amount) {
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
                return false;
            }
        }
        return true;
    }


}
