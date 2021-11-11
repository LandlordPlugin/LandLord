package biz.princeps.landlord.manager.cost;

import biz.princeps.landlord.api.ICostStrategy;
import org.bukkit.plugin.Plugin;

public class SinusStrategy implements ICostStrategy {

    private final Plugin plugin;
    private final int free;
    private final String namespace;

    public SinusStrategy(Plugin plugin, String namespace, int free) {
        this.plugin = plugin;
        this.free = free;
        this.namespace = namespace;
    }

    @Override
    public double calculate(int x) {
        double minCost = plugin.getConfig().getDouble(namespace + "SINUS.minCost");
        double multiplier = plugin.getConfig().getDouble(namespace + "SINUS.multiplier");
        double b = plugin.getConfig().getDouble(namespace + "SINUS.b");
        double c = plugin.getConfig().getDouble(namespace + "SINUS.c");

        return minCost + multiplier * Math.sin(b * (x - free) + c);
    }
}
