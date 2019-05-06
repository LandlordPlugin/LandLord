package biz.princeps.landlord.manager;

import biz.princeps.landlord.Landlord;

import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 4/7/18
 */
public class CostManager {

    private Landlord plugin;
    private COST_FUNCTION selectedFunction;

    public CostManager() {
        this.plugin = Landlord.getInstance();

        String func = plugin.getConfig().getString("Growth.function");
        try {
            selectedFunction = COST_FUNCTION.valueOf(func.toUpperCase());
        } catch (IllegalArgumentException ex) {
            // Fall back to linear
            plugin.getLogger().warning("Illegal function [" + func + "] detected! The plugin will not operate flawless!");
            selectedFunction = COST_FUNCTION.LINEAR;
        }
    }

    public COST_FUNCTION getSelectedFunction() {
        return selectedFunction;
    }

    public double calculateCost(UUID uuid) {
        return this.calculateCost(plugin.getWgHandler().getRegionCountOfPlayer(uuid));
    }

    /**
     * Calculates cost for the next land to claim
     *
     * @param x current amt of lands
     * @return amount to pay for next land
     */
    public double calculateCost(int x) {
        int freeLands = plugin.getConfig().getInt("Freelands");

        if (x < freeLands)
            return 0;

        switch (selectedFunction) {

            case LIMITED:
                double minCost = plugin.getConfig().getDouble("Growth.LIMITED.minCost");
                double maxCost = plugin.getConfig().getDouble("Growth.LIMITED.maxCost");
                double multiplier = plugin.getConfig().getDouble("Growth.LIMITED.multiplier");

                double var = Math.pow(multiplier, x - freeLands);

                return maxCost - (maxCost - minCost) * var;

            case LINEAR:
                minCost = plugin.getConfig().getDouble("Growth.LINEAR.minCost");
                multiplier = plugin.getConfig().getDouble("Growth.LINEAR.multiplier");

                return minCost + multiplier * (x - freeLands);

            case EXPONENTIAL:
                minCost = plugin.getConfig().getDouble("Growth.EXPONENTIAL.minCost");
                multiplier = plugin.getConfig().getDouble("Growth.EXPONENTIAL.multiplier");
                double exponent = plugin.getConfig().getDouble("Growth.EXPONENTIAL.exponent");

                return minCost + multiplier * Math.pow(x - freeLands, exponent);

            case LOGARITHMIC:
                minCost = plugin.getConfig().getDouble("Growth.LOGARITHMIC.minCost");
                multiplier = plugin.getConfig().getDouble("Growth.LOGARITHMIC.multiplier");
                double b = plugin.getConfig().getDouble("Growth.LOGARITHMIC.b");
                double c = plugin.getConfig().getDouble("Growth.LOGARITHMIC.c");

                // # Formula: minCost + multiplier * lg (b * x + c)
                return minCost + multiplier * Math.log10(b * (x - freeLands) + c);

            case SINUS:
                minCost = plugin.getConfig().getDouble("Growth.SINUS.minCost");
                multiplier = plugin.getConfig().getDouble("Growth.SINUS.multiplier");
                b = plugin.getConfig().getDouble("Growth.SINUS.b");
                c = plugin.getConfig().getDouble("Growth.SINUS.c");

                return minCost + multiplier * Math.sin(b * (x - freeLands) + c);
        }

        return -1;
    }

    enum COST_FUNCTION {
        LIMITED,
        LINEAR,
        EXPONENTIAL,
        LOGARITHMIC,
        SINUS
    }
}
