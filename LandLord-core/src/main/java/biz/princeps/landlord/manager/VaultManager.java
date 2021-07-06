package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IVaultManager;
import biz.princeps.landlord.api.Options;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class VaultManager implements IVaultManager {

    private final Economy economy;

    private final boolean defaultBalance;
    private final boolean playerWorldBased;
    private final String worldBalance;

    public VaultManager(ILandLord plugin, Economy economy) {
        this.economy = economy;

        String worldBalance = plugin.getConfig().getString("Economy.worldBalance");
        this.defaultBalance = worldBalance.equals("default");
        this.playerWorldBased = plugin.getConfig().getBoolean("Economy.playerWorldBased");
        this.worldBalance = worldBalance;
    }

    @Override
    public double getBalance(Player player) {
        return playerWorldBased ? economy.getBalance(player, player.getWorld().getName()) :
                defaultBalance ? economy.getBalance(player) : economy.getBalance(player, worldBalance);
    }

    @Override
    public boolean hasBalance(Player player, double amount) {
        return playerWorldBased ? economy.has(player, player.getWorld().getName(), amount) :
                defaultBalance ? economy.has(player, amount) : economy.has(player, worldBalance, amount);
    }

    @Override
    public void take(Player player, double amount) {
        if (playerWorldBased) {
            economy.withdrawPlayer(player, player.getWorld().getName(), amount);
        } else {
            if (defaultBalance) {
                economy.withdrawPlayer(player, amount);
            } else {
                economy.withdrawPlayer(player, worldBalance, amount);
            }
        }
    }

    @Override
    public void give(UUID id, double amount, World world) {
        if (playerWorldBased) {
            economy.depositPlayer(Bukkit.getOfflinePlayer(id), world.getName(), amount);
        } else {
            if (defaultBalance) {
                economy.depositPlayer(Bukkit.getOfflinePlayer(id), amount);
            } else {
                economy.depositPlayer(Bukkit.getOfflinePlayer(id), worldBalance, amount);
            }
        }
    }

    @Override
    public void give(Player player, double amount) {
        if (playerWorldBased) {
            economy.depositPlayer(player, player.getWorld().getName(), amount);
        } else {
            if (defaultBalance) {
                economy.depositPlayer(player, amount);
            } else {
                economy.depositPlayer(player, worldBalance, amount);
            }
        }
    }

    /**
     * Formats a given money double to the vault style with the currency e.g. 100 Dollars
     *
     * @param money the amount which should be formatted
     * @return a formatted string
     */
    @Override
    public String format(double money) {
        return Options.isVaultEnabled() ? economy.format(money) : "-1";
    }

}
