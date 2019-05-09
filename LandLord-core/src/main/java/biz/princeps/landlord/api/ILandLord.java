package biz.princeps.landlord.api;

import co.aikar.taskchain.TaskChain;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public interface ILandLord {

    <T> TaskChain<T> newSharedChain(String name);

    <T> TaskChain<T> newChain();

    void onEnable();

    void onDisable();

    FileConfiguration getConfig();

    Logger getLogger();

    JavaPlugin getPlugin();


    IWorldGuardProxy getWGProxy();

    IMaterialsProxy getMatProxy();

    IUtilsProxy getUtilsProxy();


    IPlayerManager getPlayerManager();

    IOfferManager getOfferManager();

    ICostManager getCostManager();

    IMapManager getMapManager();

    ILangManager getLangManager();

    IVaultManager getVaultManager();

    IDelimitationManager getDelimitationManager();

    void setupPrincepsLib();
}
