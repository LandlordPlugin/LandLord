package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.LPlayer;
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
        double cost;

        switch (args.size()) {
            case 3:
                // ll giveclaims name price amount
                // used for shop give claims
                try {
                    target = args.get(0);
                    cost = args.getDouble(1);
                    amount = args.getInt(2);
                } catch (ArgumentsOutOfBoundsException e) {
                    issuer.sendUsage();
                    return;
                }

                Player player = Bukkit.getPlayer(target);
                if (player != null) {
                    if (checkPermission(player, amount)) {
                        if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), cost)) {
                            plugin.getVaultHandler().take(player.getUniqueId(), cost);
                            player.sendMessage(plugin.getLangManager().getString("Shop.success")
                                    .replace("%number%", amount + "")
                                    .replace("%cost%", plugin.getVaultHandler().format(cost)));
                            player.closeInventory();
                            addClaims(issuer, target, amount);
                        } else {
                            player.sendMessage(plugin.getLangManager().getString("Shop.notEnoughMoney")
                                    .replace("%number%", amount + "")
                                    .replace("%cost%", plugin.getVaultHandler().format(cost)));
                            player.closeInventory();
                            return;
                        }
                    }
                }
                break;

            case 2:
                //ll giveclaims name amount
                try {
                    target = args.get(0);
                    amount = args.getInt(1);
                } catch (ArgumentsOutOfBoundsException e) {
                    issuer.sendUsage();
                    return;
                }
                if (Bukkit.getPlayer(target) != null) {
                    addClaims(issuer, target, amount);

                }
                break;

            case 1:
                //ll giveclaims amount
                try {
                    amount = args.getInt(0);
                } catch (ArgumentsOutOfBoundsException e) {
                    issuer.sendUsage();
                    return;
                }

                if (issuer.isPlayer()) {
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
        LPlayer lPlayer = plugin.getPlayerManager().get(target);

        if (Bukkit.getPlayer(target).isOnline() && lPlayer != null) {
            lPlayer.addClaims(amount);
            Bukkit.getPlayer(target).sendMessage(lm.getString("Commands.GiveClaims.success").replace("%amount%", String.valueOf(amount)));
        } else {
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
    }

    // TODO clean up this mess Claim#hasLimitPermission!! This is so bad style, i get ill just by thinking about i wrote this bullshit
    private boolean checkPermission(Player player, int amount) {
        if (!player.hasPermission("landlord.limit.override")) {
            int claimcount = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();
            List<Integer> limitlist = plugin.getConfig().getIntegerList("limits");

            int highestAllowedLandCount = -1;
            for (Integer integer : limitlist) {
                if (player.hasPermission("landlord.limit." + integer)) {
                    highestAllowedLandCount = integer;
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
