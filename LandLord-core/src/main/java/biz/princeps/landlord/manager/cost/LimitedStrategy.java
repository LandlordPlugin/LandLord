package biz.princeps.landlord.manager.cost;

import biz.princeps.landlord.api.ICostStrategy;
import org.bukkit.plugin.java.JavaPlugin;

public class LimitedStrategy implements ICostStrategy {

    private JavaPlugin plugin;
    private int free;
    private String namespace;

    public LimitedStrategy(JavaPlugin plugin, String namespace, int free) {
        this.plugin = plugin;
        this.free = free;
        this.namespace = namespace;
    }

    @Override
    public double calculate(int x) {
        double minCost = plugin.getConfig().getDouble(namespace + "LIMITED.minCost");
        double maxCost = plugin.getConfig().getDouble(namespace + "LIMITED.maxCost");
        double multiplier = plugin.getConfig().getDouble(namespace + "LIMITED.multiplier");

        double var = Math.pow(multiplier, x - free);

        return maxCost - (maxCost - minCost) * var;
    }
}