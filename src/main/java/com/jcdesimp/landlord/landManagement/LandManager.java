package com.jcdesimp.landlord.landManagement;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.Data;
import com.jcdesimp.landlord.persistantData.LandFlag;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by spatium on 10.06.17.
 */
public class LandManager {


    /**
     * Factory method that creates a new OwnedLand instance given an owner name and chunk
     *
     * @param owner The owner of the land
     * @param c     The chunk this land represents
     * @return OwnedLand
     */
    public OwnedLand createNewLand(UUID owner, Chunk c) {
        Data data = new Data(c.getWorld().getName(), c.getX(), c.getZ());
        OwnedLand lnd = new OwnedLand(data);
        lnd.setOwner(owner);
        lnd.setLandId(Landlord.getInstance().getDatabase().getFirstFreeLandID());
        lnd.setFlags(getDefaultFlags(lnd.getLandId()));
        lnd.setFriends(new ArrayList<>());
        return lnd;
    }

    public OwnedLand getApplicableLand(Location l) {
        return getLandFromCache(l.getWorld().getName(), l.getChunk().getX(), l.getChunk().getZ());
    }

    public OwnedLand getApplicableLand(Chunk chunk) {
        return getLandFromCache(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }


    /**
     * Gets land from the database
     *
     * @param x         coord of chunk
     * @param z         coord of chunk
     * @param worldName of chunk
     * @return OwnedLand instance
     */
    public OwnedLand getLandFromCache(String worldName, int x, int z) {
        Data data = new Data(worldName, x, z);
        OwnedLand toFind = null;
        try {
            toFind = Landlord.getInstance().getDatabase().getLand(data).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return toFind;
    }

    private static Set<String> flags = Landlord.getInstance().getFlagManager().getRegisteredFlags().keySet();

    public static List<LandFlag> getDefaultFlags(int landid) {
        List<LandFlag> list = new ArrayList<>();

        for (String identifier : flags) {
            LandFlag flag = new LandFlag(landid, identifier, false, true);
            list.add(flag);
        }
        return list;
    }
}
