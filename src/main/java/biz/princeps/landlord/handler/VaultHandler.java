package biz.princeps.landlord.handler;

import biz.princeps.landlord.Landlord;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * Created by spatium on 17.07.17.
 */
public class VaultHandler {

    private Economy economy;

    public VaultHandler(Economy economy) {
        this.economy = economy;
    }

    public double getBalance(UUID id) {
        return economy.getBalance(Bukkit.getOfflinePlayer(id));
    }

    public boolean hasBalance(UUID id, double amount) {
        return economy.has(Bukkit.getOfflinePlayer(id), amount);
    }

    public void take(UUID id, double amount) {
        economy.withdrawPlayer(Bukkit.getOfflinePlayer(id), amount);
    }

    public void give(UUID id, double amount) {
        economy.depositPlayer(Bukkit.getOfflinePlayer(id), amount);
    }

    public String format(double calculatedCost) {
        return economy.format(calculatedCost);
    }
}
