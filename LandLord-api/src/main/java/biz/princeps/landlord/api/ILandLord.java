package biz.princeps.landlord.api;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public interface ILandLord {

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

    IMobProxy getMobProxy();
}
