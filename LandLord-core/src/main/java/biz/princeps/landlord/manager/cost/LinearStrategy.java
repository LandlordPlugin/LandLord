package biz.princeps.landlord.manager.cost;

import biz.princeps.landlord.api.ICostStrategy;
import org.bukkit.plugin.Plugin;

public class LinearStrategy implements ICostStrategy {

    private final Plugin plugin;
    private final int free;
    private final String namespace;

    public LinearStrategy(Plugin plugin, String namespace, int free) {
        this.plugin = plugin;
        this.free = free;
        this.namespace = namespace;
    }

    @Override
    public double calculate(int x) {
        double minCost = plugin.getConfig().getDouble(namespace + "LINEAR.minCost");
        double multiplier = plugin.getConfig().getDouble(namespace + "LINEAR.multiplier");

        return minCost + multiplier * (x - free);
    }
}
