package biz.princeps.landlord.manager.cost;

import biz.princeps.landlord.api.ICostManager;
import biz.princeps.landlord.api.ICostStrategy;
import biz.princeps.landlord.api.ILandLord;


/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 4/7/18
 */
public abstract class ACostManager implements ICostManager {

    protected final ILandLord plugin;
    protected final ICostStrategy strategy;
    protected final int free;

    public ACostManager(ILandLord plugin, int free, String namespace) {
        this.plugin = plugin;
        this.free = free;
        String func = plugin.getConfig().getString(namespace + "function");

        switch (func.toLowerCase()) {
            default:
                plugin.getLogger().warning("Illegal function [" + func + "] detected! The plugin will default to " +
                        "linear");
            case "linear":
                this.strategy = new LinearStrategy(plugin, namespace, free);
                break;
            case "exponential":
                this.strategy = new ExponentialStrategy(plugin, namespace, free);
                break;
            case "limited":
                this.strategy = new LimitedStrategy(plugin, namespace, free);
                break;
            case "logarithmic":
                this.strategy = new LogarithmicStrategy(plugin, namespace, free);
                break;
            case "sinus":
                this.strategy = new SinusStrategy(plugin, namespace, free);
                break;
        }
    }
}
