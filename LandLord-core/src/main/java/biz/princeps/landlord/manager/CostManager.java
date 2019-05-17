package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.ICostManager;
import biz.princeps.landlord.api.ILandLord;

import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 4/7/18
 */
public class CostManager implements ICostManager {

    private ILandLord plugin;
    private ICostStrategy strategy;
    private int freeLands;

    public CostManager(ILandLord plugin) {
        this.plugin = plugin;
        this.freeLands = plugin.getConfig().getInt("Freelands");
        String func = plugin.getConfig().getString("Growth.function");

        switch (func.toLowerCase()) {
            default:
                plugin.getLogger().warning("Illegal function [" + func + "] detected! The plugin will default to " +
                        "linear");
            case "linear":
                this.strategy = new LinearStrategy();
                break;
            case "exponential":
                this.strategy = new ExponentialStrategy();
                break;
            case "limited":
                this.strategy = new LimitedStrategy();
                break;
            case "logarithmic":
                this.strategy = new LogarithmicStrategy();
                break;
            case "sinus":
                this.strategy = new SinusStrategy();
                break;
        }
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
        int freeLands = plugin.getConfig().getInt("Freelands");

        if (x < freeLands)
            return 0;

        return strategy.calculate(x);
    }

    // Strategies - self explanatory
    class LimitedStrategy implements ICostStrategy {

        @Override
        public double calculate(int x) {
            double minCost = plugin.getConfig().getDouble("Growth.LIMITED.minCost");
            double maxCost = plugin.getConfig().getDouble("Growth.LIMITED.maxCost");
            double multiplier = plugin.getConfig().getDouble("Growth.LIMITED.multiplier");

            double var = Math.pow(multiplier, x - freeLands);

            return maxCost - (maxCost - minCost) * var;
        }
    }

    class LinearStrategy implements ICostStrategy {

        @Override
        public double calculate(int x) {
            double minCost = plugin.getConfig().getDouble("Growth.LINEAR.minCost");
            double multiplier = plugin.getConfig().getDouble("Growth.LINEAR.multiplier");

            return minCost + multiplier * (x - freeLands);
        }
    }

    class ExponentialStrategy implements ICostStrategy {

        @Override
        public double calculate(int x) {
            double minCost = plugin.getConfig().getDouble("Growth.EXPONENTIAL.minCost");
            double multiplier = plugin.getConfig().getDouble("Growth.EXPONENTIAL.multiplier");
            double exponent = plugin.getConfig().getDouble("Growth.EXPONENTIAL.exponent");

            return minCost + multiplier * Math.pow(x - freeLands, exponent);
        }
    }

    class LogarithmicStrategy implements ICostStrategy {

        @Override
        public double calculate(int x) {
            double minCost = plugin.getConfig().getDouble("Growth.LOGARITHMIC.minCost");
            double multiplier = plugin.getConfig().getDouble("Growth.LOGARITHMIC.multiplier");
            double b = plugin.getConfig().getDouble("Growth.LOGARITHMIC.b");
            double c = plugin.getConfig().getDouble("Growth.LOGARITHMIC.c");

            // # Formula: minCost + multiplier * lg (b * x + c)
            return minCost + multiplier * Math.log10(b * (x - freeLands) + c);
        }
    }

    class SinusStrategy implements ICostStrategy {

        @Override
        public double calculate(int x) {
            double minCost = plugin.getConfig().getDouble("Growth.SINUS.minCost");
            double multiplier = plugin.getConfig().getDouble("Growth.SINUS.multiplier");
            double b = plugin.getConfig().getDouble("Growth.SINUS.b");
            double c = plugin.getConfig().getDouble("Growth.SINUS.c");

            return minCost + multiplier * Math.sin(b * (x - freeLands) + c);
        }
    }

    interface ICostStrategy {
        double calculate(int x);
    }
}