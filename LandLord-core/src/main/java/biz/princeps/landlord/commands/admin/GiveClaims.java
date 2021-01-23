package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IVaultManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class GiveClaims extends LandlordCommand {

    private final IVaultManager vault = plugin.getVaultManager();

    public GiveClaims(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.GiveClaims.name"),
                pl.getConfig().getString("CommandSettings.GiveClaims.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.GiveClaims.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.GiveClaims.aliases")));
    }

    @Override
    public void onCommand(Properties issuer, Arguments args) {
        String target;
        int amount;
        double cost;

        if (!Options.isVaultEnabled() || !Options.enabled_shop()) {
            plugin.getLogger().info("Vault or the shop system is disabled! Therefore you cant execute giveclaims.");
            return;
        }

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
                        if (vault.hasBalance(player, cost)) {

                            vault.take(player, cost);
                            lm.sendMessage(player, plugin.getLangManager().getString("Shop.success")
                                    .replace("%number%", amount + "")
                                    .replace("%cost%", vault.format(cost)));
                            player.closeInventory();
                            addClaims(issuer, target, amount);
                        } else {
                            lm.sendMessage(player, plugin.getLangManager().getString("Shop.notEnoughMoney")
                                    .replace("%number%", amount + "")
                                    .replace("%cost%", vault.format(cost)));
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
                } catch (Exception e) {
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
        Player player = Bukkit.getPlayer(target);
        IPlayer lPlayer = player != null ? plugin.getPlayerManager().get(player.getUniqueId()) : null;

        if (player != null && player.isOnline() && lPlayer != null) {
            lPlayer.addClaims(amount);
            lm.sendMessage(player, lm.getString(player, "Commands.GiveClaims.success")
                    .replace("%amount%", String.valueOf(amount)));
        } else {
            plugin.getPlayerManager().getOffline(target, (offline) -> {
                if (offline != null) {
                    offline.addClaims(amount);
                    plugin.getPlayerManager().save(offline, true);
                    OfflinePlayer op = Bukkit.getOfflinePlayer(offline.getUuid());

                    if (op.isOnline()) {
                        lm.sendMessage(op.getPlayer(), lm.getString(op.getPlayer(), "Commands.GiveClaims.success").replace("%amount%",
                                String.valueOf(amount)));
                    }
                } else {
                    if (issuer.getPlayer() != null) {
                        lm.sendMessage(issuer.getPlayer(), lm.getString(issuer.getPlayer(), "Commands.GiveClaims.noPlayer"));
                    }
                }
            });
        }
    }

    private boolean checkPermission(Player player, int amount) {
        final int claimcount = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();

        final int highestAllowedLandCount = plugin.getPlayerManager().getMaxClaimPermission(player);

        if (claimcount + amount > highestAllowedLandCount) {
            lm.sendMessage(player, plugin.getLangManager().getString("Shop.notAllowed"));
            return false;
        }
        return true;
    }
}
