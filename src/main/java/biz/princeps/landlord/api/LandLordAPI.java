package biz.princeps.landlord.api;

import biz.princeps.landlord.handler.WorldGuardHandler;
import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public interface LandLordAPI {

    IPlayerManager getPlayerManager();

    OwnedLand getLand(Location loc);

    OwnedLand getLand(Chunk chunk);

    OwnedLand getLand(ProtectedRegion protectedRegion);

    List<ProtectedRegion> getRegions(UUID id, World world);

    WorldGuardHandler getWgHandler();

    boolean isLLRegion(String a);

    IOfferManager getOfferManager();
}


