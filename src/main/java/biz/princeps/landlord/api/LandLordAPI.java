package biz.princeps.landlord.api;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.manager.LPlayerManager;
import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class LandLordAPI {

    private static LandLordAPI ourInstance;

    public static LandLordAPI getInstance() {
        if (ourInstance == null)
            ourInstance = new LandLordAPI();
        return ourInstance;
    }

    private LandLordAPI() {
    }

    private Landlord pl = Landlord.getInstance();

    public LPlayerManager getPlayerManager() {
        return pl.getPlayerManager();
    }

    public int getRegionCount(Player player) {
        int regionCount = pl.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegionCountOfPlayer(pl.getWgHandler().getWG().wrapOfflinePlayer(player));
        return regionCount;
    }

    public void claim(Chunk chunk, UUID uuid) {
        pl.getWgHandler().claim(chunk, uuid);
    }

    public void unclaim(World world, String name) {
        pl.getWgHandler().unclaim(world, name);
    }

    public OwnedLand getLand(Location loc) {
        return pl.getWgHandler().getRegion(loc);
    }

    public List<ProtectedRegion> getRegions(UUID id, World world) {
        return pl.getWgHandler().getRegions(id, world);
    }

}


