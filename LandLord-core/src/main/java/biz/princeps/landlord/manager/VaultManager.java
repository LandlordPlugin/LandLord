package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.IVaultManager;
import biz.princeps.landlord.api.Options;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class VaultManager implements IVaultManager {

    private final Economy economy;

    public VaultManager(Economy economy) {
        this.economy = economy;
    }

    @Override
    public double getBalance(UUID id) {
        return economy.getBalance(Bukkit.getOfflinePlayer(id));
    }

    @Override
    public boolean hasBalance(UUID id, double amount) {
        return economy.has(Bukkit.getOfflinePlayer(id), amount);
    }

    @Override
    public void take(UUID id, double amount) {
        economy.withdrawPlayer(Bukkit.getOfflinePlayer(id), amount);
    }

    @Override
    public void give(UUID id, double amount) {
        EconomyResponse economyResponse = economy.depositPlayer(Bukkit.getOfflinePlayer(id), amount);
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
