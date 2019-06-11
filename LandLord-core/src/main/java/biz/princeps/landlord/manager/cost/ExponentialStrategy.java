package biz.princeps.landlord.manager.cost;

import biz.princeps.landlord.api.ICostStrategy;
import org.bukkit.plugin.java.JavaPlugin;

public class ExponentialStrategy implements ICostStrategy {

    private JavaPlugin plugin;
    private int free;
    private String namespace;

    public ExponentialStrategy(JavaPlugin plugin, String namespace, int free) {
        this.plugin = plugin;
        this.free = free;
        this.namespace = namespace;
    }

    @Override
    public double calculate(int x) {
        double minCost = plugin.getConfig().getDouble(namespace + "EXPONENTIAL.minCost");
        double multiplier = plugin.getConfig().getDouble(namespace + "EXPONENTIAL.multiplier");
        double exponent = plugin.getConfig().getDouble(namespace + "EXPONENTIAL.exponent");

        return minCost + multiplier * Math.pow(x - free, exponent);
    }
}