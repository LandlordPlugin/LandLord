package biz.princeps.landlord;

import biz.princeps.landlord.handler.VaultHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public abstract class ALandLord extends JavaPlugin {

    protected Landlord core;

    protected boolean checkDependencies() {
        if (!Bukkit.getVersion().contains("1.13.2")) {
            haltPlugin("Invalid spigot version detected! LandLord requires 1.13.2");
            return false;
        }

        if (!getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            haltPlugin("ProtocolLib not found! Please ensure you have the correct version of ProtocolLib in order to use LandLord");
            return false;
        }
        if (getVault() == null) {
            getLogger().warning("Vault not found! Not all features of landlord are working.");
        } else {
            core.setVaultHandler(new VaultHandler(getVault()));
        }
        return true;
    }

    protected void haltPlugin(String warning) {
        getLogger().warning(warning);
        this.getPluginLoader().disablePlugin(this);
    }

    public Economy getVault() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        if (plugin == null) {
            return null;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        return (rsp == null ? null : rsp.getProvider());
    }
}
