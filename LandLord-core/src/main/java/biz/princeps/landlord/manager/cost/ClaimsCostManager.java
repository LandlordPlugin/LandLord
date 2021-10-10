package biz.princeps.landlord.manager.cost;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.util.JavaUtils;
import jdk.jfr.internal.LogLevel;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.logging.Level;

public class ClaimsCostManager extends ACostManager {

    public ClaimsCostManager(ILandLord plugin) {
        super(plugin, plugin.getConfig().getInt("Claims.free"), "Claims.calc.");
    }

    @Override
    public double calculateCost(UUID uuid) {
        if (Bukkit.getPlayer(uuid) == null) {
            return this.calculateCost(plugin.getPlayerManager().getOfflineSync(uuid).getClaims());
        } else {
            return this.calculateCost(plugin.getPlayerManager().get(uuid).getClaims());
        }
    }

    /**
     * Calculates cost for the next land to claim based on the amount x of owned lands
     *
     * @param x current amt of lands
     * @return amount to pay for next land
     */
    @Override
    public double calculateCost(int x) {
        if (x < free)
            return 0;

        return strategy.calculate(x);
    }

    public double calculateCost(int x, int times) {
        double cost = 0;
        if (times < 0) {
            plugin.getLogger().log(Level.INFO, "times " + times);
            for (int i = times; i < 0; i++) {
                cost += calculateCost(x + i);
                // plugin.getLogger().log(Level.INFO, "\tCost for (" + (x + i) + ") is " + cost);
            }
        } else {
            plugin.getLogger().log(Level.INFO, "times " + times);
            for (int i = 0; i < times; i++) {
                cost += calculateCost(x + i);
                //plugin.getLogger().log(Level.INFO, "\tCost for (" + (x + i) + ") is " + cost);

            }
        }
        return JavaUtils.round(cost, 2);
    }
}
