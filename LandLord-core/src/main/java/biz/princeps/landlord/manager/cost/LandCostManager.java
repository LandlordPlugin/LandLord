package biz.princeps.landlord.manager.cost;

import biz.princeps.landlord.api.ILandLord;

import java.util.UUID;

public class LandCostManager extends ACostManager {

    public LandCostManager(ILandLord plugin) {
        super(plugin, plugin.getConfig().getInt("Freelands"), "Growth.");
    }

    @Override
    public double calculateCost(UUID uuid) {
        return this.calculateCost(plugin.getWGManager().getRegionCount(uuid));
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
}
