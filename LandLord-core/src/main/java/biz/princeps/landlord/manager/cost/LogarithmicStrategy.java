package biz.princeps.landlord.manager.cost;

import biz.princeps.landlord.api.ICostStrategy;
import org.bukkit.plugin.java.JavaPlugin;

public class LogarithmicStrategy implements ICostStrategy {

    private JavaPlugin plugin;
    private int free;
    private String namespace;

    public LogarithmicStrategy(JavaPlugin plugin, String namespace, int free) {
        this.plugin = plugin;
        this.free = free;
        this.namespace = namespace;
    }

    @Override
    public double calculate(int x) {
        double minCost = plugin.getConfig().getDouble(namespace + "LOGARITHMIC.minCost");
        double multiplier = plugin.getConfig().getDouble(namespace + "LOGARITHMIC.multiplier");
        double b = plugin.getConfig().getDouble(namespace + "LOGARITHMIC.b");
        double c = plugin.getConfig().getDouble(namespace + "LOGARITHMIC.c");

        // # Formula: minCost + multiplier * lg (b * x + c)
        return minCost + multiplier * Math.log10(b * (x - free) + c);
    }
}